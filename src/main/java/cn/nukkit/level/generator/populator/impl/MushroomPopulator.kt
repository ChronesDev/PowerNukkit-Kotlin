package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class MushroomPopulator @JvmOverloads constructor(private val type: Int = -1) : PopulatorCount() {
    @Override
    fun populateCount(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom, chunk: FullChunk) {
        val x = chunkX shl 4 or random.nextBoundedInt(16)
        val z = chunkZ shl 4 or random.nextBoundedInt(16)
        val y = getHighestWorkableBlock(level, x, z, chunk)
        if (y != -1) {
            BigMushroom(type).generate(level, random, Vector3(x, y, z))
        }
    }

    @Override
    protected fun getHighestWorkableBlock(level: ChunkManager?, x: Int, z: Int, chunk: FullChunk): Int {
        var x = x
        var z = z
        var y: Int
        x = x and 0xF
        z = z and 0xF
        y = 254
        while (y > 0) {
            val b: Int = chunk.getBlockId(x, y, z)
            if (b == Block.DIRT || b == Block.GRASS) {
                break
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1
            }
            --y
        }
        return ++y
    }
}