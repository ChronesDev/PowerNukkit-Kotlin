package cn.nukkit.block

import cn.nukkit.item.enchantment.Enchantment

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockGravel : BlockFallable() {
    @get:Override
    override val id: Int
        get() = GRAVEL

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val name: String
        get() = "Gravel"

    @Override
    override fun getDrops(item: Item): Array<Item> {
        val enchantment: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        var fortune = 0
        if (enchantment != null) {
            fortune = enchantment.getLevel()
        }
        val nukkitRandom = NukkitRandom()
        when (fortune) {
            0 -> if (nukkitRandom.nextRange(0, 9) === 0) {
                return arrayOf<Item>(Item.get(ItemID.FLINT, 0, 1))
            }
            1 -> if (nukkitRandom.nextRange(0, 6) === 0) {
                return arrayOf<Item>(Item.get(ItemID.FLINT, 0, 1))
            }
            2 -> if (nukkitRandom.nextRange(0, 3) === 0) {
                return arrayOf<Item>(Item.get(ItemID.FLINT, 0, 1))
            }
            3 -> return arrayOf<Item>(Item.get(ItemID.FLINT, 0, 1))
            else -> return arrayOf<Item>(Item.get(ItemID.FLINT, 0, 1))
        }
        return arrayOf<Item>(toItem())
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}