package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class PopulatorGroundFire : PopulatorSurfaceBlock() {
    @Override
    protected fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, NETHERRACK, chunk)
    }

    @Override
    protected fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int {
        return FIRE shl Block.DATA_BITS
    }

    @Override
    protected fun placeBlock(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk, random: NukkitRandom?) {
        super.placeBlock(x, y, z, id, chunk, random)
        chunk.setBlockLight(x, y, z, Block.light.get(FIRE))
    }

    @Override
    protected fun getHighestWorkableBlock(level: ChunkManager?, x: Int, z: Int, chunk: FullChunk): Int {
        var y: Int
        y = 0
        while (y <= 127) {
            val b: Int = chunk.getBlockId(x, y, z)
            if (b == Block.AIR) {
                break
            }
            ++y
        }
        return if (y == 0) -1 else y
    }
}