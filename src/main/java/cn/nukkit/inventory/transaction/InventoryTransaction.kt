package cn.nukkit.inventory.transaction

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class InventoryTransaction(source: Player?, actions: List<InventoryAction>, init: Boolean) {
    var creationTime: Long = 0
        private set
    protected var hasExecuted = false
    protected var source: Player? = null
    protected var inventories: Set<Inventory> = HashSet()
    protected var actions: List<InventoryAction> = ArrayList()

    constructor(source: Player?, actions: List<InventoryAction>) : this(source, actions, true) {}

    protected fun init(source: Player?, actions: List<InventoryAction>) {
        creationTime = System.currentTimeMillis()
        this.source = source
        for (action in actions) {
            addAction(action)
        }
    }

    fun getSource(): Player? {
        return source
    }

    fun getInventories(): Set<Inventory> {
        return inventories
    }

    @get:Since("1.3.0.0-PN")
    val actionList: List<Any>
        get() = actions

    fun getActions(): Set<InventoryAction> {
        return HashSet(actions)
    }

    fun addAction(action: InventoryAction) {
        if (action is SlotChangeAction) {
            val slotChangeAction: SlotChangeAction = action as SlotChangeAction
            val iterator: ListIterator<InventoryAction> = actions.listIterator()
            while (iterator.hasNext()) {
                val existingAction: InventoryAction = iterator.next()
                if (existingAction is SlotChangeAction) {
                    val existingSlotChangeAction: SlotChangeAction = existingAction as SlotChangeAction
                    if (!existingSlotChangeAction.getInventory().equals(slotChangeAction.getInventory())) continue
                    val existingSource: Item = existingSlotChangeAction.getSourceItem()
                    val existingTarget: Item = existingSlotChangeAction.getTargetItem()
                    if (existingSlotChangeAction.getSlot() === slotChangeAction.getSlot()
                            && slotChangeAction.getSourceItem().equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        iterator.set(SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSlotChangeAction.getSourceItem(), slotChangeAction.getTargetItem()))
                        action.onAddToTransaction(this)
                        return
                    } else if (existingSlotChangeAction.getSlot() === slotChangeAction.getSlot() && slotChangeAction.getSourceItem().equals(existingSource, existingSource.hasMeta(), existingSource.hasCompoundTag())
                            && slotChangeAction.getTargetItem().equals(existingTarget, existingTarget.hasMeta(), existingTarget.hasCompoundTag())) {
                        existingSource.setCount(existingSource.getCount() + slotChangeAction.getSourceItem().getCount())
                        existingTarget.setCount(existingTarget.getCount() + slotChangeAction.getTargetItem().getCount())
                        iterator.set(SlotChangeAction(existingSlotChangeAction.getInventory(), existingSlotChangeAction.getSlot(), existingSource, existingTarget))
                        return
                    }
                }
            }
        }
        actions.add(action)
        action.onAddToTransaction(this)
    }

    /**
     * This method should not be used by plugins, it's used to add tracked inventories for InventoryActions
     * involving inventories.
     *
     * @param inventory to add
     */
    fun addInventory(inventory: Inventory?) {
        inventories.add(inventory)
    }

    protected fun matchItems(needItems: List<Item?>, haveItems: List<Item?>): Boolean {
        for (action in actions) {
            if (action.getTargetItem().getId() !== Item.AIR) {
                needItems.add(action.getTargetItem())
            }
            if (!action.isValid(source)) {
                return false
            }
            if (action.getSourceItem().getId() !== Item.AIR) {
                haveItems.add(action.getSourceItem())
            }
        }
        for (needItem in ArrayList(needItems)) {
            for (haveItem in ArrayList(haveItems)) {
                if (needItem.equals(haveItem)) {
                    val amount: Int = Math.min(haveItem.getCount(), needItem.getCount())
                    needItem.setCount(needItem.getCount() - amount)
                    haveItem.setCount(haveItem.getCount() - amount)
                    if (haveItem.getCount() === 0) {
                        haveItems.remove(haveItem)
                    }
                    if (needItem.getCount() === 0) {
                        needItems.remove(needItem)
                        break
                    }
                }
            }
        }
        return haveItems.isEmpty() && needItems.isEmpty()
    }

    protected fun sendInventories() {
        for (action in actions) {
            if (action is SlotChangeAction) {
                val sca: SlotChangeAction = action as SlotChangeAction
                sca.getInventory().sendSlot(sca.getSlot(), source)
            } else if (action is TakeLevelAction) {
                source.sendExperienceLevel()
            }
        }
    }

    fun canExecute(): Boolean {
        val haveItems: List<Item> = ArrayList()
        val needItems: List<Item> = ArrayList()
        return matchItems(needItems, haveItems) && actions.size() > 0 && haveItems.size() === 0 && needItems.size() === 0
    }

    protected fun callExecuteEvent(): Boolean {
        val ev = InventoryTransactionEvent(this)
        source.getServer().getPluginManager().callEvent(ev)
        var from: SlotChangeAction? = null
        var to: SlotChangeAction? = null
        var who: Player? = null
        for (action in actions) {
            if (action !is SlotChangeAction) {
                continue
            }
            val slotChange: SlotChangeAction = action as SlotChangeAction
            if (slotChange.getInventory().getHolder() is Player) {
                who = slotChange.getInventory().getHolder() as Player
            }
            if (from == null) {
                from = slotChange
            } else {
                to = slotChange
            }
        }
        if (who != null && to != null) {
            if (from.getTargetItem().getCount() > from.getSourceItem().getCount()) {
                from = to
            }
            val ev2 = InventoryClickEvent(who, from.getInventory(), from.getSlot(), from.getSourceItem(), from.getTargetItem())
            source.getServer().getPluginManager().callEvent(ev2)
            if (ev2.isCancelled()) {
                return false
            }
        }
        return !ev.isCancelled()
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Always returns false if the execution is not possible")
    fun execute(): Boolean {
        if (hasExecuted() || !canExecute()) {
            sendInventories()
            return false
        }
        if (!callExecuteEvent()) {
            sendInventories()
            return false
        }
        for (action in actions) {
            if (!action.onPreExecute(source)) {
                sendInventories()
                return false
            }
            if (action is SlotChangeAction) {
                if (source.isPlayer()) {
                    val player: Player? = source as Player?
                    if (player.isSurvival()) {
                        val slot: Int = (action as SlotChangeAction).getSlot()
                        if (slot == 36 || slot == 37 || slot == 38 || slot == 39) {
                            if (action.getSourceItem().hasEnchantment(Enchantment.ID_BINDING_CURSE)) {
                                sendInventories()
                                return false
                            }
                        }
                    }
                }
            }
        }
        for (action in actions) {
            if (action.execute(source)) {
                action.onExecuteSuccess(source)
            } else {
                action.onExecuteFail(source)
            }
        }
        hasExecuted = true
        return true
    }

    fun hasExecuted(): Boolean {
        return hasExecuted
    }

    init {
        if (init) {
            init(source, actions)
        }
    }
}