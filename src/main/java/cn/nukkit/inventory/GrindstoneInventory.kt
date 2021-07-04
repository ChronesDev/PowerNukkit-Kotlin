package cn.nukkit.inventory

import cn.nukkit.Player

@PowerNukkitOnly
class GrindstoneInventory @PowerNukkitOnly constructor(playerUI: PlayerUIInventory, position: Position) : FakeBlockUIComponent(playerUI, InventoryType.GRINDSTONE, OFFSET, position) {
    private var resultExperience = 0

    @Override
    override fun close(who: Player) {
        onClose(who)
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
        clear(SLOT_FIRST_ITEM)
        clear(SLOT_SECOND_ITEM)
        who.resetCraftingGridType()
    }

    @Override
    override fun onOpen(who: Player) {
        super.onOpen(who)
        who.craftingType = Player.CRAFTING_GRINDSTONE
    }

    @PowerNukkitOnly
    fun getFirstItem(): Item {
        return getItem(SLOT_FIRST_ITEM)
    }

    @PowerNukkitOnly
    fun getSecondItem(): Item {
        return getItem(SLOT_SECOND_ITEM)
    }

    @PowerNukkitOnly
    fun getResult(): Item {
        return getItem(2)
    }

    @PowerNukkitOnly
    fun setFirstItem(item: Item?, send: Boolean): Boolean {
        return setItem(SLOT_FIRST_ITEM, item, send)
    }

    @PowerNukkitOnly
    fun setFirstItem(item: Item?): Boolean {
        return setFirstItem(item, true)
    }

    @PowerNukkitOnly
    fun setSecondItem(item: Item?, send: Boolean): Boolean {
        return setItem(SLOT_SECOND_ITEM, item, send)
    }

    @PowerNukkitOnly
    fun setSecondItem(item: Item?): Boolean {
        return setSecondItem(item, true)
    }

    @PowerNukkitOnly
    fun setResult(item: Item?, send: Boolean): Boolean {
        return setItem(2, item, send)
    }

    @PowerNukkitOnly
    fun setResult(item: Item?): Boolean {
        return setResult(item, true)
    }

    @Override
    override fun onSlotChange(index: Int, before: Item?, send: Boolean) {
        try {
            if (index > 1) {
                return
            }
            updateResult(send)
        } finally {
            super.onSlotChange(index, before, send)
        }
    }

    fun updateResult(send: Boolean): Boolean {
        var firstItem: Item = getFirstItem()
        var secondItem: Item = getSecondItem()
        if (!firstItem.isNull() && !secondItem.isNull() && firstItem.getId() !== secondItem.getId()) {
            setResult(Item.get(0), send)
            setResultExperience(0)
            return false
        }
        if (firstItem.isNull()) {
            val air: Item = firstItem
            firstItem = secondItem
            secondItem = air
        }
        if (firstItem.isNull()) {
            setResult(Item.get(0), send)
            setResultExperience(0)
            return false
        }
        if (firstItem.getId() === ItemID.ENCHANTED_BOOK) {
            if (secondItem.isNull()) {
                setResult(Item.get(ItemID.BOOK, 0, firstItem.getCount()), send)
                recalculateResultExperience()
            } else {
                setResultExperience(0)
                setResult(Item.get(0), send)
            }
            return false
        }
        val result: Item = firstItem.clone()
        var tag: CompoundTag? = result.getNamedTag()
        if (tag == null) tag = CompoundTag()
        tag.remove("ench")
        result.setCompoundTag(tag)
        if (!secondItem.isNull() && firstItem.getMaxDurability() > 0) {
            val first: Int = firstItem.getMaxDurability() - firstItem.getDamage()
            val second: Int = secondItem.getMaxDurability() - secondItem.getDamage()
            val reduction: Int = first + second + firstItem.getMaxDurability() * 5 / 100
            val resultingDamage: Int = Math.max(firstItem.getMaxDurability() - reduction + 1, 0)
            result.setDamage(resultingDamage)
        }
        setResult(result, send)
        recalculateResultExperience()
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun recalculateResultExperience() {
        if (getResult().isNull()) {
            setResultExperience(0)
            return
        }
        val firstItem: Item = getFirstItem()
        val secondItem: Item = getSecondItem()
        if (!firstItem.hasEnchantments() && !secondItem.hasEnchantments()) {
            setResultExperience(0)
            return
        }
        var resultExperience: Int = Stream.of(firstItem, secondItem)
                .flatMap { item ->
                    // Support stacks of enchanted items and skips invalid stacks (e.g. negative stacks, enchanted air)
                    if (item.isNull()) {
                        return@flatMap Stream.empty()
                    } else if (item.getCount() === 1) {
                        return@flatMap Arrays.stream(item.getEnchantments())
                    } else {
                        val enchantments: Array<Array<Enchantment>> = arrayOfNulls<Array<Enchantment>>(item.getCount())
                        Arrays.fill(enchantments, item.getEnchantments())
                        return@flatMap Arrays.stream(enchantments).flatMap(Arrays::stream)
                    }
                }
                .mapToInt { enchantment -> enchantment.getMinEnchantAbility(enchantment.getLevel()) }
                .sum()
        resultExperience = ThreadLocalRandom.current().nextInt(
                NukkitMath.ceilDouble(resultExperience.toDouble() / 2),
                resultExperience + 1
        )
        setResultExperience(resultExperience)
    }

    @Override
    override fun getItem(index: Int): Item {
        var index = index
        if (index < 0 || index > 3) {
            return Item.get(0)
        }
        if (index == 2) {
            index = SLOT_RESULT
        }
        return super.getItem(index)
    }

    @Override
    override fun setItem(index: Int, item: Item?, send: Boolean): Boolean {
        var index = index
        if (index < 0 || index > 3) {
            return false
        }
        if (index == 2) {
            index = SLOT_RESULT
        }
        return super.setItem(index, item, send)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getResultExperience(): Int {
        return resultExperience
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setResultExperience(returnLevels: Int) {
        resultExperience = returnLevels
    }

    companion object {
        @PowerNukkitOnly
        val OFFSET = 16
        private const val SLOT_FIRST_ITEM = 0
        private const val SLOT_SECOND_ITEM = 1
        private val SLOT_RESULT = 50 - OFFSET

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @API(usage = API.Usage.INCUBATING, definition = API.Definition.INTERNAL)
        val GRINDSTONE_EQUIPMENT_UI_SLOT = OFFSET + SLOT_FIRST_ITEM

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @API(usage = API.Usage.INCUBATING, definition = API.Definition.INTERNAL)
        val GRINDSTONE_INGREDIENT_UI_SLOT = OFFSET + SLOT_SECOND_ITEM
    }
}