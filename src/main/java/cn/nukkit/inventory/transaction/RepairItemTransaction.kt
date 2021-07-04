package cn.nukkit.inventory.transaction

import cn.nukkit.Player

@Since("1.4.0.0-PN")
class RepairItemTransaction @Since("1.4.0.0-PN") constructor(source: Player?, actions: List<InventoryAction>) : InventoryTransaction(source, actions) {
    private var inputItem: Item? = null
    private var materialItem: Item? = null
    private var outputItem: Item? = null

    @get:Since("1.4.0.0-PN")
    var cost = 0
        private set

    @Override
    override fun canExecute(): Boolean {
        val inventory: Inventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID) ?: return false
        val anvilInventory: AnvilInventory = inventory as AnvilInventory
        return (inputItem != null && outputItem != null && inputItem.equals(anvilInventory.getInputSlot(), true, true)
                && (!hasMaterial() || materialItem.equals(anvilInventory.getMaterialSlot(), true, true))
                && checkRecipeValid())
    }

    @Override
    override fun execute(): Boolean {
        if (this.hasExecuted() || !canExecute()) {
            this.source.removeAllWindows(false)
            this.sendInventories()
            return false
        }
        val inventory: AnvilInventory = getSource().getWindowById(Player.ANVIL_WINDOW_ID) as AnvilInventory
        if (inventory.getCost() !== cost && !this.source.isCreative()) {
            this.source.getServer().getLogger().debug("Got unexpected cost " + inventory.getCost().toString() + " from " + this.source.getName().toString() + "(expected " + cost.toString() + ")")
        }
        val event = RepairItemEvent(inventory, inputItem, outputItem, materialItem, cost, this.source)
        this.source.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            this.source.removeAllWindows(false)
            this.sendInventories()
            return true
        }
        for (action in this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source)
            } else {
                action.onExecuteFail(this.source)
            }
        }
        val holder: FakeBlockMenu = inventory.getHolder()
        val block: Block = this.source.level.getBlock(holder.getFloorX(), holder.getFloorY(), holder.getFloorZ())
        if (block.getId() === Block.ANVIL) {
            val oldDamage = if (block.getDamage() >= 8) 2 else if (block.getDamage() >= 4) 1 else 0
            var newDamage = if (!this.source.isCreative() && ThreadLocalRandom.current().nextInt(100) < 12) oldDamage + 1 else oldDamage
            val ev = AnvilDamageEvent(block, oldDamage, newDamage, DamageCause.USE, this.source)
            ev.setCancelled(oldDamage == newDamage)
            this.source.getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                newDamage = ev.getNewDamage()
                if (newDamage > 2) {
                    this.source.level.setBlock(block, Block.get(Block.AIR), true)
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_BREAK)
                } else {
                    if (newDamage < 0) {
                        newDamage = 0
                    }
                    if (newDamage != oldDamage) {
                        block.setDamage(newDamage shl 2 or (block.getDamage() and 0x3))
                        this.source.level.setBlock(block, block, true)
                    }
                    this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE)
                }
            } else {
                this.source.level.addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_USE)
            }
        }
        if (!this.source.isCreative()) {
            this.source.setExperience(this.source.getExperience(), this.source.getExperienceLevel() - event.getCost())
        }
        return true
    }

    @Override
    override fun addAction(action: InventoryAction) {
        super.addAction(action)
        if (action is RepairItemAction) {
            when ((action as RepairItemAction).getType()) {
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT -> inputItem = action.getTargetItem()
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT -> outputItem = action.getSourceItem()
                NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL -> materialItem = action.getTargetItem()
            }
        }
    }

    private fun checkRecipeValid(): Boolean {
        var cost = 0
        var baseRepairCost: Int = inputItem.getRepairCost()
        if (isMapRecipe) {
            if (!matchMapRecipe()) {
                return false
            }
            baseRepairCost = 0
        } else if (hasMaterial()) {
            baseRepairCost += materialItem.getRepairCost()
            if (inputItem.getMaxDurability() !== -1 && matchRepairItem()) {
                val maxRepairDamage: Int = inputItem.getMaxDurability() / 4
                var repairDamage: Int = Math.min(inputItem.getDamage(), maxRepairDamage)
                if (repairDamage <= 0) {
                    return false
                }
                var damage: Int = inputItem.getDamage()
                while (repairDamage > 0 && cost < materialItem.getCount()) {
                    damage = damage - repairDamage
                    repairDamage = Math.min(damage, maxRepairDamage)
                    cost++
                }
                if (outputItem.getDamage() !== damage) {
                    return false
                }
            } else {
                val consumeEnchantedBook = materialItem.getId() === Item.ENCHANTED_BOOK && materialItem.hasEnchantments()
                if (!consumeEnchantedBook && (inputItem.getMaxDurability() === -1 || inputItem.getId() !== materialItem.getId())) {
                    return false
                }
                if (!consumeEnchantedBook && inputItem.getMaxDurability() !== -1) {
                    var damage: Int = inputItem.getDamage() - inputItem.getMaxDurability() + materialItem.getDamage() - inputItem.getMaxDurability() * 12 / 100 + 1
                    if (damage < 0) {
                        damage = 0
                    }
                    if (damage < inputItem.getDamage()) {
                        if (outputItem.getDamage() !== damage) {
                            return false
                        }
                        cost += 2
                    }
                }
                val enchantments: Int2IntMap = Int2IntOpenHashMap()
                enchantments.defaultReturnValue(-1)
                for (enchantment in inputItem.getEnchantments()) {
                    enchantments.put(enchantment.getId(), enchantment.getLevel())
                }
                var hasCompatibleEnchantments = false
                var hasIncompatibleEnchantments = false
                for (materialEnchantment in materialItem.getEnchantments()) {
                    val enchantment: Enchantment = inputItem.getEnchantment(materialEnchantment.getId())
                    val inputLevel = if (enchantment != null) enchantment.getLevel() else 0
                    val materialLevel: Int = materialEnchantment.getLevel()
                    var outputLevel = if (inputLevel == materialLevel) materialLevel + 1 else Math.max(materialLevel, inputLevel)
                    var canEnchant = materialEnchantment.canEnchant(inputItem) || inputItem.getId() === Item.ENCHANTED_BOOK
                    for (inputEnchantment in inputItem.getEnchantments()) {
                        if (inputEnchantment.getId() !== materialEnchantment.getId() && !materialEnchantment.isCompatibleWith(inputEnchantment)) {
                            canEnchant = false
                            cost++
                        }
                    }
                    if (!canEnchant) {
                        hasIncompatibleEnchantments = true
                    } else {
                        hasCompatibleEnchantments = true
                        if (outputLevel > materialEnchantment.getMaxLevel()) {
                            outputLevel = materialEnchantment.getMaxLevel()
                        }
                        enchantments.put(materialEnchantment.getId(), outputLevel)
                        var rarityFactor: Int
                        rarityFactor = when (materialEnchantment.getRarity()) {
                            COMMON -> 1
                            UNCOMMON -> 2
                            RARE -> 4
                            VERY_RARE -> 8
                            else -> 8
                        }
                        if (consumeEnchantedBook) {
                            rarityFactor = Math.max(1, rarityFactor / 2)
                        }
                        cost += rarityFactor * Math.max(0, outputLevel - inputLevel)
                        if (inputItem.getCount() > 1) {
                            cost = 40
                        }
                    }
                }
                val outputEnchantments: Array<Enchantment> = outputItem.getEnchantments()
                if (hasIncompatibleEnchantments && !hasCompatibleEnchantments || enchantments.size() !== outputEnchantments.size) {
                    return false
                }
                for (enchantment in outputEnchantments) {
                    if (enchantments.get(enchantment.getId()) !== enchantment.getLevel()) {
                        return false
                    }
                }
            }
        }
        var renameCost = 0
        if (!inputItem.getCustomName().equals(outputItem.getCustomName())) {
            if (outputItem.getCustomName().length() > 30) {
                return false
            }
            renameCost = 1
            cost += renameCost
        }
        this.cost = baseRepairCost + cost
        if (renameCost == cost && renameCost > 0 && this.cost >= 40) {
            this.cost = 39
        }
        if (baseRepairCost < 0 || cost < 0 || cost == 0 && !isMapRecipe || this.cost > 39 && !this.source.isCreative()) {
            return false
        }
        var nextBaseRepairCost: Int = inputItem.getRepairCost()
        if (!isMapRecipe) {
            if (hasMaterial() && nextBaseRepairCost < materialItem.getRepairCost()) {
                nextBaseRepairCost = materialItem.getRepairCost()
            }
            if (renameCost == 0 || renameCost != cost) {
                nextBaseRepairCost = 2 * nextBaseRepairCost + 1
            }
        }
        if (outputItem.getRepairCost() !== nextBaseRepairCost) {
            this.source.getServer().getLogger().debug("Got unexpected base cost " + outputItem.getRepairCost().toString() + " from " + this.source.getName().toString() + "(expected " + nextBaseRepairCost.toString() + ")")
            return false
        }
        return true
    }

    private fun hasMaterial(): Boolean {
        return materialItem != null && !materialItem.isNull()
    }

    private val isMapRecipe: Boolean
        private get() = (hasMaterial() && (inputItem.getId() === Item.MAP || inputItem.getId() === Item.EMPTY_MAP)
                && (materialItem.getId() === Item.EMPTY_MAP || materialItem.getId() === Item.PAPER || materialItem.getId() === Item.COMPASS))

    private fun matchMapRecipe(): Boolean {
        if (inputItem.getId() === Item.EMPTY_MAP) {
            return inputItem.getDamage() !== 2 && materialItem.getId() === Item.COMPASS // locator
                    && outputItem.getId() === Item.EMPTY_MAP && outputItem.getDamage() === 2 && outputItem.getCount() === 1
        } else if (inputItem.getId() === Item.MAP && outputItem.getDamage() === inputItem.getDamage()) {
            if (materialItem.getId() === Item.COMPASS) { // locator
                return inputItem.getDamage() !== 2 && outputItem.getId() === Item.MAP && outputItem.getCount() === 1
            } else if (materialItem.getId() === Item.EMPTY_MAP) { // clone
                return outputItem.getId() === Item.MAP && outputItem.getCount() === 2
            } else if (materialItem.getId() === Item.PAPER && materialItem.getCount() >= 8) { // zoom out
                return inputItem.getDamage() < 3 && outputItem.getId() === Item.MAP && outputItem.getCount() === 1
            }
        }
        return false
    }

    private fun matchRepairItem(): Boolean {
        when (inputItem.getId()) {
            Item.LEATHER_CAP, Item.LEATHER_TUNIC, Item.LEATHER_PANTS, Item.LEATHER_BOOTS -> return materialItem.getId() === Item.LEATHER
            Item.WOODEN_SWORD, Item.WOODEN_PICKAXE, Item.WOODEN_SHOVEL, Item.WOODEN_AXE, Item.WOODEN_HOE -> return materialItem.getId() === Item.PLANKS
            Item.IRON_SWORD, Item.IRON_PICKAXE, Item.IRON_SHOVEL, Item.IRON_AXE, Item.IRON_HOE, Item.IRON_HELMET, Item.IRON_CHESTPLATE, Item.IRON_LEGGINGS, Item.IRON_BOOTS, Item.CHAIN_HELMET, Item.CHAIN_CHESTPLATE, Item.CHAIN_LEGGINGS, Item.CHAIN_BOOTS -> return materialItem.getId() === Item.IRON_INGOT
            Item.GOLD_SWORD, Item.GOLD_PICKAXE, Item.GOLD_SHOVEL, Item.GOLD_AXE, Item.GOLD_HOE, Item.GOLD_HELMET, Item.GOLD_CHESTPLATE, Item.GOLD_LEGGINGS, Item.GOLD_BOOTS -> return materialItem.getId() === Item.GOLD_INGOT
            Item.DIAMOND_SWORD, Item.DIAMOND_PICKAXE, Item.DIAMOND_SHOVEL, Item.DIAMOND_AXE, Item.DIAMOND_HOE, Item.DIAMOND_HELMET, Item.DIAMOND_CHESTPLATE, Item.DIAMOND_LEGGINGS, Item.DIAMOND_BOOTS -> return materialItem.getId() === Item.DIAMOND
            Item.NETHERITE_SWORD, Item.NETHERITE_PICKAXE, Item.NETHERITE_SHOVEL, Item.NETHERITE_AXE, Item.NETHERITE_HOE, Item.NETHERITE_HELMET, Item.NETHERITE_CHESTPLATE, Item.NETHERITE_LEGGINGS, Item.NETHERITE_BOOTS -> return materialItem.getId() === Item.NETHERITE_INGOT
            Item.TURTLE_SHELL -> return materialItem.getId() === Item.SCUTE
            Item.ELYTRA -> return materialItem.getId() === Item.PHANTOM_MEMBRANE
        }
        return false
    }

    @Since("1.4.0.0-PN")
    fun getInputItem(): Item? {
        return inputItem
    }

    @Since("1.4.0.0-PN")
    fun getMaterialItem(): Item? {
        return materialItem
    }

    @Since("1.4.0.0-PN")
    fun getOutputItem(): Item? {
        return outputItem
    }

    companion object {
        @Since("1.4.0.0-PN")
        fun checkForRepairItemPart(actions: List<InventoryAction?>): Boolean {
            for (action in actions) {
                if (action is RepairItemAction) {
                    return true
                }
            }
            return false
        }
    }
}