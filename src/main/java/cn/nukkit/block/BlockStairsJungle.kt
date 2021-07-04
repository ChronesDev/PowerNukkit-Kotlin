package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsJungle @JvmOverloads constructor(meta: Int = 0) : BlockStairsWood(meta) {
    @get:Override
    override val id: Int
        get() = JUNGLE_WOOD_STAIRS

    @get:Override
    override val name: String
        get() = "Jungle Wood Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR
}