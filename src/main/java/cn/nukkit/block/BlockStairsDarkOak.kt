package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsDarkOak @JvmOverloads constructor(meta: Int = 0) : BlockStairsWood(meta) {
    @get:Override
    override val id: Int
        get() = DARK_OAK_WOOD_STAIRS

    @get:Override
    override val name: String
        get() = "Dark Oak Wood Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR
}