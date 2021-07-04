package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author Nukkit Project Team
 */
class BlockClay : BlockSolid() {
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
    override val id: Int
        get() = CLAY_BLOCK

    @get:Override
    override val name: String
        get() = "Clay Block"

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                ItemClay(0, 4)
        )
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CLAY_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}