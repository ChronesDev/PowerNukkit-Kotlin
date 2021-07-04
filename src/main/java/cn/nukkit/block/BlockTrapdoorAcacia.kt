package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockTrapdoorAcacia @JvmOverloads constructor(meta: Int = 0) : BlockTrapdoor(meta) {
    @get:Override
    override val id: Int
        get() = ACACIA_TRAPDOOR

    @get:Override
    override val name: String
        get() = "Acacia Trapdoor"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}