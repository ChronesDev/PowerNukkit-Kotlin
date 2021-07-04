package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockGlass : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = GLASS

    @get:Override
    override val name: String
        get() = "Glass"

    @get:Override
    override val resistance: Double
        get() = 1.5

    @get:Override
    override val hardness: Double
        get() = 0.3

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR
}