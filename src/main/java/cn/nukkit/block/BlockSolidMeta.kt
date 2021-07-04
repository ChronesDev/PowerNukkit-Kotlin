package cn.nukkit.block

import cn.nukkit.utils.BlockColor

abstract class BlockSolidMeta protected constructor(meta: Int) : BlockMeta(meta) {
    @get:Override
    override val isSolid: Boolean
        get() = true

    @get:Override
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR
}