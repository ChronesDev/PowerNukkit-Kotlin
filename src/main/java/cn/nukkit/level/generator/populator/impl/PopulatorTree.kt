package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class PopulatorTree @JvmOverloads constructor(private val type: Int = BlockSapling.OAK) : PopulatorCount() {
    private var level: ChunkManager? = null
    @Override
    fun populateCount(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk?) {
        this.level = level
        val x: Int = NukkitMath.randomRange(random, chunkX shl 4, (chunkX shl 4) + 15)
        val z: Int = NukkitMath.randomRange(random, chunkZ shl 4, (chunkZ shl 4) + 15)
        val y = getHighestWorkableBlock(x, z)
        if (y < 3) {
            return
        }
        ObjectTree.growTree(this.level, x, y, z, random, type)
    }

    private fun getHighestWorkableBlock(x: Int, z: Int): Int {
        var y: Int
        y = 254
        while (y > 0) {
            val b: Int = level.getBlockIdAt(x, y, z)
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