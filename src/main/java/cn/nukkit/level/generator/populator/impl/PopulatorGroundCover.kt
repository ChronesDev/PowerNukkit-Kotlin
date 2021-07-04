package cn.nukkit.level.generator.populator.impl

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_
 */
class PopulatorGroundCover : Populator() {
    @Override
    fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk) {
        //reverse iteration to 0 is faster
        for (x in 15 downTo 0) {
            for (z in 15 downTo 0) {
                val realBiome: Biome = EnumBiome.getBiome(chunk.getBiomeId(x, z))
                if (realBiome is CoveredBiome) {
                    (realBiome as CoveredBiome).doCover(x, z, chunk)
                }
            }
        }
    }

    companion object {
        val STONE: Int = BlockID.STONE shl Block.DATA_BITS
    }
}