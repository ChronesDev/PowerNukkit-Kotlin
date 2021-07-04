package cn.nukkit.level.generator.populator.type

import cn.nukkit.block.BlockID

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class Populator : BlockID {
    abstract fun populate(level: ChunkManager?, chunkX: Int, chunkZ: Int, random: NukkitRandom?, chunk: FullChunk?)
    protected fun getHighestWorkableBlock(level: ChunkManager?, x: Int, z: Int, chunk: FullChunk): Int {
        return chunk.getHighestBlockAt(x and 0xF, z and 0xF)
    }
}