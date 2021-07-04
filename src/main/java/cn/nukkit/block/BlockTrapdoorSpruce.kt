package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockTrapdoorSpruce @JvmOverloads constructor(meta: Int = 0) : BlockTrapdoor(meta) {
    @get:Override
    override val id: Int
        get() = SPRUCE_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Spruce Trapdoor"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SPRUCE_BLOCK_COLOR
}