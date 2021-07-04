package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/6
 */
class BlockGlassPane : BlockThin() {
    @get:Override
    override val name: String
        get() = "Glass Pane"

    @get:Override
    override val id: Int
        get() = GLASS_PANE

    @get:Override
    override val resistance: Double
        get() = 1.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val hardness: Double
        get() = 0.3

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}