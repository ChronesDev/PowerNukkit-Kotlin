package cn.nukkit.block

import cn.nukkit.utils.BlockColor

class BlockDriedKelpBlock : BlockSolid() {
    @get:Override
    override val id: Int
        get() = DRIED_KELP_BLOCK

    @get:Override
    override val name: String
        get() = "Dried Kelp Block"

    @get:Override
    override val hardness: Double
        get() = 0.5f

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GREEN_BLOCK_COLOR
}