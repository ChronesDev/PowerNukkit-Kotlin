package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class PopulatorLilyPad : PopulatorSurfaceBlock() {
    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, STILL_WATER, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return WATER_LILY shl Block.DATA_BITS
    }
}