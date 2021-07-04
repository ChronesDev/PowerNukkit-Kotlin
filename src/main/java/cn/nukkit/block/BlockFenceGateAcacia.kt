package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFenceGateAcacia @JvmOverloads constructor(meta: Int = 0) : BlockFenceGate(meta) {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_ACACIA

    @get:Override
    override val name: String
        get() = "Acacia Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}