package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class AnvilInventory(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.ANVIL, OFFSET, position) {
    private var cost = 0
    private var newItemName: String? = null

    @NonNull
    private var currentResult: Item = Item.get(0)

    /*
    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        try {
            if (index <= 1) {
                updateResult();
            }
        } finally {
            super.onSlotChange(index, before, send);
        }
    }
     */
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Experimenting the new implementation by Nukkit")
    fun updateResult() {
        val target: Item = getFirstItem()
        val sacrifice: Item = getSecondItem()
        if (target.isNull() && sacrifice.isNull()) {
            setResult(Item.get(0))
            setLevelCost(0)
            return
        }
        setLevelCost(1)
        var extraCost = 0
        var costHelper = 0
        val repairMaterial = getRepairMaterial(target)
        var result: Item = target.clone()
        val levelCost = getRepairCost(result) + if (sacrifice.isNull()) 0 else getRepairCost(sacrifice)
        val enchantmentMap: Map<Integer, Enchantment> = LinkedHashMap()
        for (enchantment in target.getEnchantments()) {
            enchantmentMap.put(enchantment.getId(), enchantment)
        }
        if (!sacrifice.isNull()) {
            val enchantedBook = sacrifice.getId() === Item.ENCHANTED_BOOK && sacrifice.getEnchantments().length > 0
            var repair: Int
            var repair2: Int
            var repair3: Int
            if (result.getMaxDurability() !== -1 && sacrifice.getId() === repairMaterial) {
                repair = Math.min(result.getDamage(), result.getMaxDurability() / 4)
                if (repair <= 0) {
                    setResult(Item.get(0))
                    setLevelCost(0)
                    return
                }
                repair2 = 0
                while (repair > 0 && repair2 < sacrifice.getCount()) {
                    repair3 = result.getDamage() - repair
                    result.setDamage(repair3)
                    ++extraCost
                    repair = Math.min(result.getDamage(), result.getMaxDurability() / 4)
                    ++repair2
                }
            } else {
                if (!enchantedBook && (result.getId() !== sacrifice.getId() || result.getMaxDurability() === -1)) {
                    setResult(Item.get(0))
                    setLevelCost(0)
                    return
                }
                if (result.getMaxDurability() !== -1 && !enchantedBook) {
                    repair = target.getMaxDurability() - target.getDamage()
                    repair2 = sacrifice.getMaxDurability() - sacrifice.getDamage()
                    repair3 = repair2 + result.getMaxDurability() * 12 / 100
                    val totalRepair = repair + repair3
                    var finalDamage: Int = result.getMaxDurability() - totalRepair + 1
                    if (finalDamage < 0) {
                        finalDamage = 0
                    }
                    if (finalDamage < result.getDamage()) {
                        result.setDamage(finalDamage)
                        extraCost += 2
                    }
                }
                val sacrificeEnchantments: Array<Enchantment> = sacrifice.getEnchantments()
                var compatibleFlag = false
                var incompatibleFlag = false
                val sacrificeEnchIter: Iterator<Enchantment> = Arrays.stream(sacrificeEnchantments).iterator()
                iter@ while (true) {
                    var sacrificeEnchantment: Enchantment
                    do {
                        if (!sacrificeEnchIter.hasNext()) {
                            if (incompatibleFlag && !compatibleFlag) {
                                setResult(Item.get(0))
                                setLevelCost(0)
                                return
                            }
                            break@iter
                        }
                        sacrificeEnchantment = sacrificeEnchIter.next()
                    } while (sacrificeEnchantment == null)
                    val resultEnchantment: Enchantment = result.getEnchantment(sacrificeEnchantment.id)
                    val targetLevel = if (resultEnchantment != null) resultEnchantment.getLevel() else 0
                    var resultLevel: Int = sacrificeEnchantment.getLevel()
                    resultLevel = if (targetLevel == resultLevel) resultLevel + 1 else Math.max(resultLevel, targetLevel)
                    var compatible: Boolean = sacrificeEnchantment.isItemAcceptable(target)
                    if (playerUI.getHolder().isCreative() || target.getId() === Item.ENCHANTED_BOOK) {
                        compatible = true
                    }
                    val targetEnchIter: Iterator<Enchantment> = Stream.of(target.getEnchantments()).iterator()
                    while (targetEnchIter.hasNext()) {
                        val targetEnchantment: Enchantment = targetEnchIter.next()
                        if (targetEnchantment.id !== sacrificeEnchantment.id && (!sacrificeEnchantment.isCompatibleWith(targetEnchantment) || !targetEnchantment.isCompatibleWith(sacrificeEnchantment))) {
                            compatible = false
                            ++extraCost
                        }
                    }
                    if (!compatible) {
                        incompatibleFlag = true
                    } else {
                        compatibleFlag = true
                        if (resultLevel > sacrificeEnchantment.getMaxLevel()) {
                            resultLevel = sacrificeEnchantment.getMaxLevel()
                        }
                        enchantmentMap.put(sacrificeEnchantment.getId(), Enchantment.getEnchantment(sacrificeEnchantment.getId()).setLevel(resultLevel))
                        var rarity = 0
                        val weight: Int = sacrificeEnchantment.getWeight()
                        rarity = if (weight >= 10) {
                            1
                        } else if (weight >= 5) {
                            2
                        } else if (weight >= 2) {
                            4
                        } else {
                            8
                        }
                        if (enchantedBook) {
                            rarity = Math.max(1, rarity / 2)
                        }
                        extraCost += rarity * Math.max(0, resultLevel - targetLevel)
                        if (target.getCount() > 1) {
                            extraCost = 40
                        }
                    }
                }
            }
        }
        if (StringUtil.isNullOrEmpty(newItemName)) {
            if (target.hasCustomName()) {
                costHelper = 1
                extraCost += costHelper
                result.clearCustomName()
            }
        } else {
            costHelper = 1
            extraCost += costHelper
            result.setCustomName(newItemName)
        }
        setLevelCost(levelCost + extraCost)
        if (extraCost <= 0) {
            result = Item.get(0)
        }
        if (costHelper == extraCost && costHelper > 0 && getLevelCost() >= 40) {
            setLevelCost(39)
        }
        if (getLevelCost() >= 40 && !this.playerUI.getHolder().isCreative()) {
            result = Item.get(0)
        }
        if (!result.isNull()) {
            var repairCost = getRepairCost(result)
            if (!sacrifice.isNull() && repairCost < getRepairCost(sacrifice)) {
                repairCost = getRepairCost(sacrifice)
            }
            if (costHelper != extraCost || costHelper == 0) {
                repairCost = repairCost * 2 + 1
            }
            var namedTag: CompoundTag? = result.getNamedTag()
            if (namedTag == null) {
                namedTag = CompoundTag()
            }
            namedTag.putInt("RepairCost", repairCost)
            namedTag.remove("ench")
            result.setNamedTag(namedTag)
            if (!enchantmentMap.isEmpty()) {
                result.addEnchantment(enchantmentMap.values().toArray(Enchantment.EMPTY_ARRAY))
            }
        }
        setResult(result)
    }

    @Override
    override fun onClose(who: Player) {
        super.onClose(who)
        who.craftingType = Player.CRAFTING_SMALL
        var drops: Array<Item> = arrayOf<Item>(getFirstItem(), getSecondItem())
        drops = who.getInventory().addItem(drops)
        for (drop in drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().add(0.5, 0.5, 0.5), drop)
            }
        }
        clear(TARGET)
        clear(SACRIFICE)
        who.resetCraftingGridType()
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        who.craftingType = Player.CRAFTING_ANVIL
    }

    /*
    @Override
    public Item getItem(int index) {
        if (index < 0 || index > 3) {
            return Item.get(0);
        }
        if (index == 2) {
            return getResult();
        }
        
        return super.getItem(index);
    }
    
    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index > 3) {
            return false;
        }
        
        if (index == 2) {
            return setResult(item);
        }
        
        return super.setItem(index, item, send);
    }
     */
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "NukkitX added the samething with other name.", by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "getInputSlot()")
    fun getFirstItem(): Item {
        return getItem(TARGET)
    }

    @Since("1.4.0.0-PN")
    fun getInputSlot(): Item {
        return this.getItem(TARGET)
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "NukkitX added the samething with other name.", by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "getMaterialSlot()")
    fun getSecondItem(): Item {
        return getItem(SACRIFICE)
    }

    @Since("1.4.0.0-PN")
    fun getMaterialSlot(): Item {
        return this.getItem(SACRIFICE)
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "NukkitX added the samething with other name.", by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "getOutputSlot()")
    fun getResult(): Item {
        //return currentResult.clone();
        return getOutputSlot()
    }

    @Since("1.4.0.0-PN")
    fun getOutputSlot(): Item {
        return this.getItem(RESULT)
    }

    /*
    @Override
    public void sendContents(Player... players) {
        super.sendContents(players);
        // Fixes desync when transactions are cancelled.
        for (Player player : players) {
            player.sendExperienceLevel();
        }
    }
     */
    @PowerNukkitOnly
    fun setFirstItem(item: Item?, send: Boolean): Boolean {
        return setItem(SACRIFICE, item, send)
    }

    @PowerNukkitOnly
    fun setFirstItem(item: Item?): Boolean {
        return setFirstItem(item, true)
    }

    @PowerNukkitOnly
    fun setSecondItem(item: Item?, send: Boolean): Boolean {
        return setItem(SACRIFICE, item, send)
    }

    @PowerNukkitOnly
    fun setSecondItem(item: Item?): Boolean {
        return setSecondItem(item, true)
    }

    private fun setResult(item: Item, send: Boolean): Boolean {
        return setItem(2, item, send)
    }

    private fun setResult(item: Item?): Boolean {
        if (item == null || item.isNull()) {
            currentResult = Item.get(0)
        } else {
            currentResult = item.clone()
        }
        return true
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "NukkitX added the samething with other name.", by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "getCost()")
    fun getLevelCost(): Int {
        return getCost()
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "NukkitX added the samething with other name.", by = "PowerNukkit", since = "1.4.0.0-PN", replaceWith = "setCost(int)")
    protected fun setLevelCost(levelCost: Int) {
        setCost(levelCost)
    }

    @Since("1.4.0.0-PN")
    fun getCost(): Int {
        return cost
    }

    @Since("1.4.0.0-PN")
    fun setCost(cost: Int) {
        this.cost = cost
    }

    @PowerNukkitOnly
    fun getNewItemName(): String? {
        return newItemName
    }

    @PowerNukkitOnly
    fun setNewItemName(newItemName: String?) {
        this.newItemName = newItemName
    }

    companion object {
        @Since("1.4.0.0-PN")
        val ANVIL_INPUT_UI_SLOT = 1

        @Since("1.4.0.0-PN")
        val ANVIL_MATERIAL_UI_SLOT = 2

        @Since("1.4.0.0-PN")
        val ANVIL_OUTPUT_UI_SLOT: Int = CREATED_ITEM_OUTPUT_UI_SLOT

        @PowerNukkitOnly
        val OFFSET = 1
        const val TARGET = 0
        const val SACRIFICE = 1
        val RESULT = ANVIL_OUTPUT_UI_SLOT - 1 //1: offset
        private fun getRepairCost(item: Item): Int {
            return if (item.hasCompoundTag() && item.getNamedTag().contains("RepairCost")) item.getNamedTag().getInt("RepairCost") else 0
        }

        private fun getRepairMaterial(target: Item): Int {
            return when (target.getId()) {
                ItemID.WOODEN_SWORD, ItemID.WOODEN_PICKAXE, ItemID.WOODEN_SHOVEL, ItemID.WOODEN_AXE, ItemID.WOODEN_HOE -> BlockID.PLANKS
                ItemID.IRON_SWORD, ItemID.IRON_PICKAXE, ItemID.IRON_SHOVEL, ItemID.IRON_AXE, ItemID.IRON_HOE, ItemID.IRON_HELMET, ItemID.IRON_CHESTPLATE, ItemID.IRON_LEGGINGS, ItemID.IRON_BOOTS, ItemID.CHAIN_HELMET, ItemID.CHAIN_CHESTPLATE, ItemID.CHAIN_LEGGINGS, ItemID.CHAIN_BOOTS -> ItemID.IRON_INGOT
                ItemID.GOLD_SWORD, ItemID.GOLD_PICKAXE, ItemID.GOLD_SHOVEL, ItemID.GOLD_AXE, ItemID.GOLD_HOE, ItemID.GOLD_HELMET, ItemID.GOLD_CHESTPLATE, ItemID.GOLD_LEGGINGS, ItemID.GOLD_BOOTS -> ItemID.GOLD_INGOT
                ItemID.DIAMOND_SWORD, ItemID.DIAMOND_PICKAXE, ItemID.DIAMOND_SHOVEL, ItemID.DIAMOND_AXE, ItemID.DIAMOND_HOE, ItemID.DIAMOND_HELMET, ItemID.DIAMOND_CHESTPLATE, ItemID.DIAMOND_LEGGINGS, ItemID.DIAMOND_BOOTS -> ItemID.DIAMOND
                ItemID.LEATHER_CAP, ItemID.LEATHER_TUNIC, ItemID.LEATHER_PANTS, ItemID.LEATHER_BOOTS -> ItemID.LEATHER
                ItemID.STONE_SWORD, ItemID.STONE_PICKAXE, ItemID.STONE_SHOVEL, ItemID.STONE_AXE, ItemID.STONE_HOE -> BlockID.COBBLESTONE
                ItemID.NETHERITE_SWORD, ItemID.NETHERITE_PICKAXE, ItemID.NETHERITE_SHOVEL, ItemID.NETHERITE_AXE, ItemID.NETHERITE_HOE, ItemID.NETHERITE_HELMET, ItemID.NETHERITE_CHESTPLATE, ItemID.NETHERITE_LEGGINGS, ItemID.NETHERITE_BOOTS -> ItemID.NETHERITE_INGOT
                ItemID.ELYTRA -> ItemID.PHANTOM_MEMBRANE
                else -> 0
            }
        }
    }
}