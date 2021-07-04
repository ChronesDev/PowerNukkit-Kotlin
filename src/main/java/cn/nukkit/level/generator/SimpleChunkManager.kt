package cn.nukkit.level.generator

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class SimpleChunkManager(@get:Override var seed: Long) : ChunkManager {
    @Override
    fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(x, y, z, 0)
    }

    @Override
    fun getBlockIdAt(x: Int, y: Int, z: Int, layer: Int): Int {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        return if (chunk != null) {
            chunk.getBlockId(x and 0xf, y and 0xff, z and 0xf, layer)
        } else 0
    }

    @Override
    fun getBlockStateAt(x: Int, y: Int, z: Int, layer: Int): BlockState {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        return if (chunk != null) {
            chunk.getBlockState(x and 0xf, y and 0xff, z and 0xf, layer)
        } else BlockState.AIR
    }

    @Override
    fun setBlockIdAt(x: Int, y: Int, z: Int, id: Int) {
        setBlockIdAt(x, y, z, 0, id)
    }

    @Override
    fun setBlockIdAt(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        if (chunk != null) {
            chunk.setBlockId(x and 0xf, y and 0xff, z and 0xf, layer, id)
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockAt(x: Int, y: Int, z: Int, id: Int, data: Int) {
        setBlockAtLayer(x, y, z, 0, id, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, id: Int, data: Int): Boolean {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        return if (chunk != null) {
            chunk.setBlock(x and 0xf, y and 0xff, z and 0xf, id, data)
        } else false
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, fullId: Int) {
        setBlockFullIdAt(x, y, z, 0, fullId)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, layer: Int, fullId: Int) {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        if (chunk != null) {
            chunk.setFullBlockId(x and 0xf, y and 0xff, z and 0xf, layer, fullId)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setBlockStateAt(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        return if (chunk != null) {
            chunk.setBlockStateAtLayer(x and 0xf, y and 0xff, z and 0xf, layer, state)
        } else false
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getBlockDataAt(x: Int, y: Int, z: Int): Int {
        return getBlockDataAt(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getBlockDataAt(x: Int, y: Int, z: Int, layer: Int): Int {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        return if (chunk != null) {
            chunk.getBlockData(x and 0xf, y and 0xff, z and 0xf, layer)
        } else 0
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockDataAt(x: Int, y: Int, z: Int, data: Int) {
        setBlockDataAt(x, y, z, data, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockDataAt(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        val chunk: FullChunk = this.getChunk(x shr 4, z shr 4)
        if (chunk != null) {
            chunk.setBlockData(x and 0xf, y and 0xff, z and 0xf, layer, data)
        }
    }

    @Override
    fun setChunk(chunkX: Int, chunkZ: Int) {
        setChunk(chunkX, chunkZ, null)
    }

    fun cleanChunks(seed: Long) {
        this.seed = seed
    }
}