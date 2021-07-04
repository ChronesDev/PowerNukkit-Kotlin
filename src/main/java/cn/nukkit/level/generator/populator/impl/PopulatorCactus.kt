package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class PopulatorCactus : PopulatorSurfaceBlock() {
    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, SAND, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return CACTUS shl Block.DATA_BITS or 1
    }
}