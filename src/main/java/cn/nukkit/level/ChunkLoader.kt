package cn.nukkit.level

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface ChunkLoader {
    val loaderId: Int
    val isLoaderActive: Boolean
    val position: cn.nukkit.level.Position?
    val x: Double
    val z: Double
    val level: cn.nukkit.level.Level?
    fun onChunkChanged(chunk: FullChunk?)
    fun onChunkLoaded(chunk: FullChunk?)
    fun onChunkUnloaded(chunk: FullChunk?)
    fun onChunkPopulated(chunk: FullChunk?)
    fun onBlockChanged(block: Vector3?)

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<ChunkLoader>(0)
    }
}