package cn.nukkit.level.generator.populator.type

import cn.nukkit.level.ChunkManager

/**
 * @author DaPorkchop_
 *
 * A populator that populates a single block type.
 */
abstract class PopulatorSurfaceBlock : PopulatorCount() {
    @Override
    protected override fun populateCount(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk) {
        val x: Int = random.nextBoundedInt(16)
        val z: Int = random.nextBoundedInt(16)
        val y = getHighestWorkableBlock(level, x, z, chunk)
        if (y > 0 && canStay(x, y, z, chunk)) {
            placeBlock(x, y, z, getBlockId(x, z, random, chunk), chunk, random)
        }
    }

    protected abstract fun canStay(x: Int, y: Int, z: Int, chunk: FullChunk?): Boolean
    protected abstract fun getBlockId(x: Int, z: Int, random: NukkitRandom?, chunk: FullChunk?): Int
    @Override
    protected override fun getHighestWorkableBlock(level: ChunkManager?, x: Int, z: Int, chunk: FullChunk): Int {
        var y: Int
        //start at 254 because we add one afterwards
        y = 254
        while (y >= 0) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(x, y, z))) {
                break
            }
            --y
        }
        return if (y == 0) -1 else ++y
    }

    protected fun placeBlock(x: Int, y: Int, z: Int, id: Int, chunk: FullChunk, random: NukkitRandom?) {
        chunk.setFullBlockId(x, y, z, id)
    }
}