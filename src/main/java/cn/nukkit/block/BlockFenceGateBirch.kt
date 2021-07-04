package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFenceGateBirch @JvmOverloads constructor(meta: Int = 0) : BlockFenceGate(meta) {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_BIRCH

    @get:Override
    override val name: String
        get() = "Birch Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}