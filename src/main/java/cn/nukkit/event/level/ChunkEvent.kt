package cn.nukkit.event.level

import cn.nukkit.level.format.FullChunk

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ChunkEvent(chunk: FullChunk) : LevelEvent(chunk.getProvider().getLevel()) {
    private val chunk: FullChunk
    fun getChunk(): FullChunk {
        return chunk
    }

    init {
        this.chunk = chunk
    }
}