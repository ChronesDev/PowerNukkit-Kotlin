package cn.nukkit.inventory.transaction

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class CraftingTransaction(source: Player, actions: List<InventoryAction>) : InventoryTransaction(source, actions, false) {
    protected var gridSize = 0
    protected var inputs: List<Item>? = null
    protected var secondaryOutputs: List<Item>? = null
    protected var primaryOutput: Item? = null
    protected var recipe: Recipe? = null
    protected var craftingType: Int

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isReadyToExecute = false
    fun setInput(item: Item) {
        if (inputs!!.size() < gridSize * gridSize) {
            for (existingInput in inputs!!) {
                if (existingInput.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingInput.setCount(existingInput.getCount() + item.getCount())
                    return
                }
            }
            inputs.add(item.clone())
        } else {
            throw RuntimeException("Input list is full can't add $item")
        }
    }

    val inputList: List<Any>?
        get() = inputs

    fun setExtraOutput(item: Item) {
        if (secondaryOutputs!!.size() < gridSize * gridSize) {
            secondaryOutputs.add(item.clone())
        } else {
            throw RuntimeException("Output list is full can't add $item")
        }
    }

    fun getPrimaryOutput(): Item? {
        return primaryOutput
    }

    fun setPrimaryOutput(item: Item) {
        if (primaryOutput == null) {
            primaryOutput = item.clone()
        } else if (!primaryOutput.equals(item)) {
            throw RuntimeException("Primary result item has already been set and does not match the current item (expected $primaryOutput, got $item)")
        }
    }

    fun getRecipe(): Recipe? {
        return recipe
    }

    override fun canExecute(): Boolean {
        val craftingManager: CraftingManager = source.getServer().getCraftingManager()
        val inventory: Inventory
        when (craftingType) {
            Player.CRAFTING_STONECUTTER -> recipe = craftingManager.matchStonecutterRecipe(primaryOutput)
            Player.CRAFTING_CARTOGRAPHY -> recipe = craftingManager.matchCartographyRecipe(inputs, primaryOutput, secondaryOutputs)
            Player.CRAFTING_SMITHING -> {
                inventory = source.getWindowById(Player.SMITHING_WINDOW_ID)
                if (inventory is SmithingInventory) {
                    addInventory(inventory)
                    val smithingInventory: SmithingInventory = inventory as SmithingInventory
                    val smithingRecipe: SmithingRecipe = smithingInventory.matchRecipe()
                    if (smithingRecipe != null && primaryOutput.equals(smithingRecipe.getFinalResult(smithingInventory.getEquipment()), true, true)) {
                        recipe = smithingRecipe
                    }
                }
            }
            Player.CRAFTING_ANVIL -> {
                inventory = source.getWindowById(Player.ANVIL_WINDOW_ID)
                if (inventory is AnvilInventory) {
                    val anvil: AnvilInventory = inventory as AnvilInventory
                    addInventory(anvil)
                    if (primaryOutput.equalsIgnoringEnchantmentOrder(anvil.getResult(), true)) {
                        actions.removeIf { action -> action is TakeLevelAction }
                        val takeLevel = TakeLevelAction(anvil.getLevelCost())
                        addAction(takeLevel)
                        if (takeLevel.isValid(source)) {
                            recipe = RepairRecipe(InventoryType.ANVIL, primaryOutput, inputs)
                            val uiInventory: PlayerUIInventory = source.getUIInventory()
                            actions.add(DamageAnvilAction(anvil, !source.isCreative() && ThreadLocalRandom.current().nextFloat() < 0.12f, this))
                            actions.stream()
                                    .filter { a -> a is SlotChangeAction }
                                    .map { a -> a as SlotChangeAction }
                                    .filter { a -> a.getInventory() === uiInventory }
                                    .filter { a -> a.getSlot() === 50 }
                                    .findFirst()
                                    .ifPresent { a ->
                                        // Move the set result action to the end, otherwise the result would be cleared too early
                                        actions.remove(a)
                                        actions.add(a)
                                    }
                        }
                    }
                }
                if (recipe == null) {
                    source.sendExperienceLevel()
                }
                source.getUIInventory().setItem(AnvilInventory.RESULT, Item.get(0), false)
            }
            Player.CRAFTING_GRINDSTONE -> {
                inventory = source.getWindowById(Player.GRINDSTONE_WINDOW_ID)
                if (inventory is GrindstoneInventory) {
                    val grindstone: GrindstoneInventory = inventory as GrindstoneInventory
                    addInventory(grindstone)
                    if (grindstone.updateResult(false) && primaryOutput.equals(grindstone.getResult(), true, true)) {
                        recipe = RepairRecipe(InventoryType.GRINDSTONE, primaryOutput, inputs)
                        grindstone.setResult(Item.get(0), false)
                    }
                }
            }
            else -> recipe = craftingManager.matchRecipe(inputs, primaryOutput, secondaryOutputs)
        }
        return recipe != null && super.canExecute()
    }

    protected override fun callExecuteEvent(): Boolean {
        var ev: CraftItemEvent
        this.source.getServer().getPluginManager().callEvent(CraftItemEvent(this).also { ev = it })
        return !ev.isCancelled()
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "No longer closes the inventory")
    protected override fun sendInventories() {
        super.sendInventories()
    }

    override fun execute(): Boolean {
        if (super.execute()) {
            when (primaryOutput.getId()) {
                Item.CRAFTING_TABLE -> source.awardAchievement("buildWorkBench")
                Item.WOODEN_PICKAXE -> source.awardAchievement("buildPickaxe")
                Item.FURNACE -> source.awardAchievement("buildFurnace")
                Item.WOODEN_HOE -> source.awardAchievement("buildHoe")
                Item.BREAD -> source.awardAchievement("makeBread")
                Item.CAKE -> source.awardAchievement("bakeCake")
                Item.STONE_PICKAXE, Item.GOLDEN_PICKAXE, Item.IRON_PICKAXE, Item.DIAMOND_PICKAXE -> source.awardAchievement("buildBetterPickaxe")
                Item.WOODEN_SWORD -> source.awardAchievement("buildSword")
                Item.DIAMOND -> source.awardAchievement("diamond")
            }
            return true
        }
        return false
    }

    @Since("1.3.0.0-PN")
    fun checkForCraftingPart(actions: List<InventoryAction>): Boolean {
        for (action in actions) {
            if (action is SlotChangeAction) {
                val slotChangeAction: SlotChangeAction = action as SlotChangeAction
                if (slotChangeAction.getInventory().getType() === InventoryType.UI && slotChangeAction.getSlot() === 50 &&
                        !slotChangeAction.getSourceItem().equals(slotChangeAction.getTargetItem())) {
                    return true
                }
            }
        }
        return false
    }

    init {
        craftingType = source.craftingType
        if (source.craftingType === Player.CRAFTING_STONECUTTER) {
            gridSize = 1
            inputs = ArrayList(1)
            secondaryOutputs = ArrayList(1)
        } else {
            gridSize = if (source.getCraftingGrid() is BigCraftingGrid) 3 else 2
            inputs = ArrayList()
            secondaryOutputs = ArrayList()
        }
        init(source, actions)
    }
}