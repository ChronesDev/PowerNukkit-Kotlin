package cn.nukkit.level.generator.task

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PopulationTask(level: Level, chunk: BaseFullChunk) : AsyncTask() {
    private val seed: Long
    private val level: Level?
    private var state = true
    private var centerChunk: BaseFullChunk?
    private var isPopulated = false
    val chunks: Array<BaseFullChunk?> = arrayOfNulls<BaseFullChunk>(9)
    @Override
    fun onRun() {
        syncGen(0)
    }

    private fun syncGen(i: Int) {
        if (i == chunks.size) {
            generationTask()
        } else {
            val chunk: BaseFullChunk? = chunks[i]
            if (chunk != null) {
                synchronized(chunk) { syncGen(i + 1) }
            }
        }
    }

    private fun generationTask() {
        state = false
        val generator: Generator = level.getGenerator() ?: return
        val manager: SimpleChunkManager = generator.getChunkManager() as SimpleChunkManager
        if (manager == null) {
            state = false
            return
        }
        synchronized(manager) {
            try {
                manager.cleanChunks(seed)
                var centerChunk: BaseFullChunk? = centerChunk
                if (centerChunk == null) {
                    return
                }
                var index = 0
                for (x in -1..1) {
                    var z = -1
                    while (z < 2) {
                        val ck: BaseFullChunk? = chunks[index]
                        if (ck === centerChunk) {
                            z++
                            index++
                            continue
                        }
                        if (ck == null) {
                            try {
                                chunks[index] = centerChunk.getClass().getMethod("getEmptyChunk", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType).invoke(null, centerChunk.getX() + x, centerChunk.getZ() + z) as BaseFullChunk
                            } catch (e: Exception) {
                                throw RuntimeException(e)
                            }
                        } else {
                            chunks[index] = ck
                        }
                        z++
                        index++
                    }
                }
                for (chunk in chunks) {
                    manager.setChunk(chunk.getX(), chunk.getZ(), chunk)
                    if (!chunk.isGenerated()) {
                        generator.generateChunk(chunk.getX(), chunk.getZ())
                        val newChunk: BaseFullChunk = manager.getChunk(chunk.getX(), chunk.getZ())
                        newChunk.setGenerated()
                        if (newChunk !== chunk) manager.setChunk(chunk.getX(), chunk.getZ(), newChunk)
                    }
                }
                isPopulated = centerChunk.isPopulated()
                if (!isPopulated) {
                    generator.populateChunk(centerChunk.getX(), centerChunk.getZ())
                    centerChunk = manager.getChunk(centerChunk.getX(), centerChunk.getZ())
                    centerChunk.setPopulated()
                    centerChunk.recalculateHeightMap()
                    centerChunk.populateSkyLight()
                    centerChunk.setLightPopulated()
                    this.centerChunk = centerChunk
                }
                manager.setChunk(centerChunk.getX(), centerChunk.getZ())
                index = 0
                for (x in -1..1) {
                    var z = -1
                    while (z < 2) {
                        chunks[index] = null
                        val newChunk: BaseFullChunk = manager.getChunk(centerChunk.getX() + x, centerChunk.getZ() + z)
                        if (newChunk != null) {
                            if (newChunk.hasChanged()) {
                                chunks[index] = newChunk
                            }
                        }
                        z++
                        index++
                    }
                }
                state = true
            } finally {
                manager.cleanChunks(seed)
            }
        }
    }

    @Override
    fun onCompletion(server: Server?) {
        if (level != null) {
            if (!state) {
                return
            }
            val centerChunk: BaseFullChunk = centerChunk ?: return
            for (chunk in chunks) {
                if (chunk != null) {
                    level.generateChunkCallback(chunk.getX(), chunk.getZ(), chunk)
                }
            }
            level.generateChunkCallback(centerChunk.getX(), centerChunk.getZ(), centerChunk, isPopulated)
        }
    }

    init {
        this.level = level
        centerChunk = chunk
        seed = level.getSeed()
        chunks[4] = chunk
        var i = 0
        for (z in -1..1) {
            var x = -1
            while (x <= 1) {
                if (i == 4) {
                    x++
                    i++
                    continue
                }
                val ck: BaseFullChunk = level.getChunk(chunk.getX() + x, chunk.getZ() + z, true)
                chunks[i] = ck
                x++
                i++
            }
        }
    }
}