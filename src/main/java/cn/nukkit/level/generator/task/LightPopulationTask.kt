package cn.nukkit.level.generator.task

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LightPopulationTask(level: Level, chunk: BaseFullChunk) : AsyncTask() {
    val levelId: Int
    var chunk: BaseFullChunk
    @Override
    fun onRun() {
        val chunk: BaseFullChunk = chunk.clone() ?: return
        chunk.recalculateHeightMap()
        chunk.populateSkyLight()
        chunk.setLightPopulated()
        this.chunk = chunk.clone()
    }

    @Override
    fun onCompletion(server: Server) {
        val level: Level = server.getLevel(levelId)
        val chunk: BaseFullChunk = chunk.clone()
        if (level != null) {
            if (chunk == null) {
                return
            }
            level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk)
        }
    }

    init {
        levelId = level.getId()
        this.chunk = chunk
    }
}