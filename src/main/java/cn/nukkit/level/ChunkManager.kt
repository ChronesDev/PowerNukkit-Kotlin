package cn.nukkit.level

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface ChunkManager {
    @PowerNukkitOnly
    fun getBlockIdAt(x: Int, y: Int, z: Int, layer: Int): Int
    fun getBlockIdAt(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, layer: Int, fullId: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, fullId: Int)

    @PowerNukkitOnly
    fun setBlockIdAt(x: Int, y: Int, z: Int, layer: Int, id: Int)
    fun setBlockIdAt(x: Int, y: Int, z: Int, id: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, id: Int, data: Int): Boolean

    @PowerNukkitOnly
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, id: Int): Boolean {
        return setBlockAtLayer(x, y, z, layer, id, 0)
    }

    fun setBlockAt(x: Int, y: Int, z: Int, id: Int) {
        setBlockStateAt(x, y, z, BlockState.of(id))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockStateAt(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockStateAt(x: Int, y: Int, z: Int, state: BlockState?): Boolean {
        return setBlockStateAt(x, y, z, 0, state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockStateAt(x: Int, y: Int, z: Int, layer: Int): BlockState?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockStateAt(x: Int, y: Int, z: Int): BlockState? {
        return getBlockStateAt(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun setBlockAt(x: Int, y: Int, z: Int, id: Int, data: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun getBlockDataAt(x: Int, y: Int, z: Int, layer: Int): Int

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun getBlockDataAt(x: Int, y: Int, z: Int): Int

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun setBlockDataAt(x: Int, y: Int, z: Int, layer: Int, data: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun setBlockDataAt(x: Int, y: Int, z: Int, data: Int)
    fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getChunk(@Nonnull pos: ChunkVector2): BaseFullChunk? {
        return getChunk(pos.getX(), pos.getZ())
    }

    fun setChunk(chunkX: Int, chunkZ: Int)
    fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk?)
    val seed: Long
}