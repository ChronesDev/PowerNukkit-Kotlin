package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFenceGateSpruce @JvmOverloads constructor(meta: Int = 0) : BlockFenceGate(meta) {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_SPRUCE

    @get:Override
    override val name: String
        get() = "Spruce Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SPRUCE_BLOCK_COLOR
}