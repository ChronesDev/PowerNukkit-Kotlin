package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFenceGateJungle @JvmOverloads constructor(meta: Int = 0) : BlockFenceGate(meta) {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_JUNGLE

    @get:Override
    override val name: String
        get() = "Jungle Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR
}