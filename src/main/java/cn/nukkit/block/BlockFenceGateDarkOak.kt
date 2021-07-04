package cn.nukkit.block

import cn.nukkit.utils.BlockColor

/**
 * @author xtypr
 * @since 2015/11/23
 */
class BlockFenceGateDarkOak @JvmOverloads constructor(meta: Int = 0) : BlockFenceGate(meta) {
    @get:Override
    override val id: Int
        get() = FENCE_GATE_DARK_OAK

    @get:Override
    override val name: String
        get() = "Dark Oak Fence Gate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR
}