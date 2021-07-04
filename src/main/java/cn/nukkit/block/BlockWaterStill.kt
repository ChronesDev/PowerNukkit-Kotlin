package cn.nukkit.block

import kotlin.jvm.JvmOverloads
import kotlin.Throws
import cn.nukkit.block.BlockRedstoneComparator.Mode

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockWaterStill : BlockWater {
    constructor() : super(0) {}
    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = STILL_WATER

    @get:Override
    override val name: String
        get() = "Still Water"

    @Override
    override fun getBlock(meta: Int): BlockLiquid {
        return Block.get(BlockID.STILL_WATER, meta) as BlockLiquid
    }
}