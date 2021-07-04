package cn.nukkit.level.generator

import cn.nukkit.level.format.generic.BaseFullChunk

class SingleChunkManager(seed: Long) : SimpleChunkManager(seed) {
    private var CX: Int = Integer.MAX_VALUE
    private var CZ: Int = Integer.MAX_VALUE
    private var chunk: BaseFullChunk? = null
    @Override
    fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk? {
        return if (chunkX == CX && chunkZ == CZ) {
            chunk
        } else null
    }

    @Override
    fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk?) {
        if (chunk == null) {
            this.chunk = null
            CX = Integer.MAX_VALUE
            CZ = Integer.MAX_VALUE
        } else if (this.chunk != null) {
            throw UnsupportedOperationException("Replacing chunks is not allowed behavior")
        } else {
            this.chunk = chunk
            CX = chunk.getX()
            CZ = chunk.getZ()
        }
    }

    @Override
    override fun cleanChunks(seed: Long) {
        super.cleanChunks(seed)
        chunk = null
        CX = Integer.MAX_VALUE
        CZ = Integer.MAX_VALUE
    }
}