package cn.nukkit.level.generator.task

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class GenerationTask(level: Level?, chunk: BaseFullChunk?) : AsyncTask() {
    private val level: Level?
    var state = true
    private var chunk: BaseFullChunk?
    @Override
    fun onRun() {
        val generator: Generator = level.getGenerator()
        state = false
        if (generator == null) {
            return
        }
        val manager: SimpleChunkManager = generator.getChunkManager() as SimpleChunkManager
        if (manager == null) {
            state = false
            return
        }
        manager.cleanChunks(level.getSeed())
        synchronized(manager) {
            try {
                var chunk: BaseFullChunk? = chunk
                if (chunk == null) {
                    return
                }
                synchronized(chunk) {
                    if (!chunk.isGenerated()) {
                        manager.setChunk(chunk.getX(), chunk.getZ(), chunk)
                        generator.generateChunk(chunk.getX(), chunk.getZ())
                        chunk = manager.getChunk(chunk.getX(), chunk.getZ())
                        chunk.setGenerated()
                    }
                }
                this.chunk = chunk
                state = true
            } finally {
                manager.cleanChunks(level.getSeed())
            }
        }
    }

    @Override
    fun onCompletion(server: Server?) {
        if (level != null) {
            if (!state) {
                return
            }
            val chunk: BaseFullChunk = chunk ?: return
            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk)
        }
    }

    init {
        this.chunk = chunk
        this.level = level
    }
}