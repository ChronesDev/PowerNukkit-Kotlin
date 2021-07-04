package cn.nukkit.inventory.transaction

import cn.nukkit.Player

@Getter
@Setter
@Since("1.3.1.0-PN")
class EnchantTransaction @Since("1.3.1.0-PN") constructor(source: Player?, actions: List<InventoryAction>) : InventoryTransaction(source, actions) {
    private var inputItem: Item? = null
    private var outputItem: Item? = null
    private var cost = -1

    @Override
    override fun canExecute(): Boolean {
        val inv: Inventory = getSource().getWindowById(Player.ENCHANT_WINDOW_ID) ?: return false
        val eInv: EnchantInventory = inv as EnchantInventory
        if (!getSource().isCreative()) {
            if (cost == -1 || !isLapisLazuli(eInv.getReagentSlot()) || eInv.getReagentSlot().count < cost) return false
        }
        return inputItem != null && outputItem != null && inputItem.equals(eInv.getInputSlot(), true, true)
    }

    private fun isLapisLazuli(item: Item): Boolean {
        return item is ItemDye && (item as ItemDye).isLapisLazuli()
    }

    @Override
    override fun execute(): Boolean {
        // This will validate the enchant conditions
        if (this.hasExecuted || !canExecute()) {
            source.removeAllWindows(false)
            this.sendInventories()
            return false
        }
        val inv: EnchantInventory = getSource().getWindowById(Player.ENCHANT_WINDOW_ID) as EnchantInventory
        val ev = EnchantItemEvent(inv, inputItem, outputItem, cost, source)
        source.getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            source.removeAllWindows(false)
            this.sendInventories()

            // Cancelled by plugin, means handled OK
            return true
        }
        // This will process all the slot changes
        for (a in this.actions) {
            if (a.execute(source)) {
                a.onExecuteSuccess(source)
            } else {
                a.onExecuteFail(source)
            }
        }
        if (!ev.getNewItem().equals(outputItem, true, true)) {
            // Plugin changed item, so the previous slot change is going to be invalid
            // Send the replaced item to the enchant inventory manually
            inv.setItem(0, ev.getNewItem(), true)
        }
        if (!source.isCreative()) {
            source.setExperience(source.getExperience(), source.getExperienceLevel() - ev.getXpCost())
        }
        return true
    }

    @Override
    override fun addAction(action: InventoryAction) {
        super.addAction(action)
        if (action is EnchantingAction) {
            when ((action as EnchantingAction).getType()) {
                NetworkInventoryAction.SOURCE_TYPE_ENCHANT_INPUT -> inputItem = action.getTargetItem() // Input sent as newItem
                NetworkInventoryAction.SOURCE_TYPE_ENCHANT_OUTPUT -> outputItem = action.getSourceItem() // Output sent as oldItem
                NetworkInventoryAction.SOURCE_TYPE_ENCHANT_MATERIAL -> if (action.getTargetItem().equals(Item.get(Item.AIR), false, false)) {
                    cost = action.getSourceItem().count
                } else {
                    cost = action.getSourceItem().count - action.getTargetItem().count
                }
            }
        }
    }

    @Since("1.3.1.0-PN")
    fun checkForEnchantPart(actions: List<InventoryAction?>): Boolean {
        for (action in actions) {
            if (action is EnchantingAction) return true
        }
        return false
    }
}