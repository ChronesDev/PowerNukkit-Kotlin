package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class PopulatorDoublePlant(private val type: Int) : PopulatorSurfaceBlock() {
    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureCover.ensureCover(x, y + 1, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return DOUBLE_PLANT shl Block.DATA_BITS or type
    }

    @Override
    protected fun placeBlock(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk, random: NukkitRandom?) {
        super.placeBlock(x, y, z, id, chunk, random)
        chunk.setFullBlockId(x, y + 1, z, 8 or id)
    }
}