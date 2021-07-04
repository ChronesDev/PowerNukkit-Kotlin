package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/12/6
 */
class BlockGlowstone : BlockTransparent() {
    @get:Override
    override val name: String
        get() = "Glowstone"

    @get:Override
    override val id: Int
        get() = GLOWSTONE_BLOCK

    @get:Override
    override val resistance: Double
        get() = 1.5

    @get:Override
    override val hardness: Double
        get() = 0.3

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun getDrops(item: Item): Array<Item> {
        val random = Random()
        var count: Int = 2 + random.nextInt(3)
        val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1)
        }
        return arrayOf<Item>(
                ItemGlowstoneDust(0, MathHelper.clamp(count, 1, 4))
        )
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}