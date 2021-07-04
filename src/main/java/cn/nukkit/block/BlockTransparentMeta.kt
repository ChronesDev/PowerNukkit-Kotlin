package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockTransparentMeta protected constructor(meta: Int = 0) : BlockMeta(meta) {
    @get:Override
    override val isTransparent: Boolean
        get() = true

    @get:Override
    override val color: BlockColor
        get() = BlockColor.TRANSPARENT_BLOCK_COLOR
}