package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsBirch @JvmOverloads constructor(meta: Int = 0) : BlockStairsWood(meta) {
    @get:Override
    override val id: Int
        get() = BIRCH_WOOD_STAIRS

    @get:Override
    override val name: String
        get() = "Birch Wood Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}