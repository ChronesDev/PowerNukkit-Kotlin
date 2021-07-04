package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class PopulatorMelon : PopulatorSurfaceBlock() {
    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return MELON_BLOCK shl Block.DATA_BITS
    }
}