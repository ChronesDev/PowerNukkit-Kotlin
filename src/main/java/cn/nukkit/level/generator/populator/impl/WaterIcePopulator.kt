package cn.nukkit.level.generator.populator.impl

import cn.nukkit.level.ChunkManager

class WaterIcePopulator : Populator() {
    @Override
    fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk) {
        for (x in 0..15) {
            for (z in 0..15) {
                val biome: Biome = EnumBiome.getBiome(chunk.getBiomeId(x, z))
                if (biome.isFreezing()) {
                    val topBlock: Int = chunk.getHighestBlockAt(x, z)
                    if (chunk.getBlockId(x, topBlock, z) === STILL_WATER) {
                        chunk.setBlockId(x, topBlock, z, ICE)
                    }
                }
            }
        }
    }
}