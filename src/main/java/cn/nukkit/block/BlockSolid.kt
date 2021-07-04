package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockSolid protected constructor() : Block() {
    @get:Override
    override val isSolid: Boolean
        get() = true

    @get:Override
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR
}