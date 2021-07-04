package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockTrapdoorDarkOak @JvmOverloads constructor(meta: Int = 0) : BlockTrapdoor(meta) {
    @get:Override
    override val id: Int
        get() = DARK_OAK_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Dark Oak Trapdoor"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR
}