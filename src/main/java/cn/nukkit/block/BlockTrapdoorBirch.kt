package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockTrapdoorBirch @JvmOverloads constructor(meta: Int = 0) : BlockTrapdoor(meta) {
    @get:Override
    override val id: Int
        get() = BIRCH_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Birch Trapdoor"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}