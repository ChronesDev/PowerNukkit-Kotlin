package cn.nukkit.level.format

import cn.nukkit.level.GameRules

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface LevelProvider {
    fun requestChunkTask(X: Int, Z: Int): AsyncTask?
    val path: String?
    val generator: String?
    val generatorOptions: Map<String?, Any?>?
    fun getLoadedChunk(X: Int, Z: Int): BaseFullChunk?
    fun getLoadedChunk(hash: Long): BaseFullChunk?
    fun getChunk(X: Int, Z: Int): BaseFullChunk?
    fun getChunk(X: Int, Z: Int, create: Boolean): BaseFullChunk?
    fun getEmptyChunk(x: Int, z: Int): BaseFullChunk?
    fun saveChunks()
    fun saveChunk(X: Int, Z: Int)
    fun saveChunk(X: Int, Z: Int, chunk: FullChunk?)
    fun unloadChunks()
    fun loadChunk(X: Int, Z: Int): Boolean
    fun loadChunk(X: Int, Z: Int, create: Boolean): Boolean
    fun unloadChunk(X: Int, Z: Int): Boolean
    fun unloadChunk(X: Int, Z: Int, safe: Boolean): Boolean
    fun isChunkGenerated(X: Int, Z: Int): Boolean
    fun isChunkPopulated(X: Int, Z: Int): Boolean
    fun isChunkLoaded(X: Int, Z: Int): Boolean
    fun isChunkLoaded(hash: Long): Boolean
    fun setChunk(chunkX: Int, chunkZ: Int, chunk: FullChunk?)
    val name: String?
    var isRaining: Boolean
    var rainTime: Int
    var isThundering: Boolean
    var thunderTime: Int
    var currentTick: Long
    var time: Long
    var seed: Long
    var spawn: Vector3?
    val loadedChunks: Map<Long?, cn.nukkit.level.format.FullChunk?>?
    fun doGarbageCollection()
    fun doGarbageCollection(time: Long) {}
    val level: Level?
    fun close()
    fun saveLevelData()
    fun updateLevelName(name: String?)
    val gamerules: cn.nukkit.level.GameRules?
    fun setGameRules(rules: GameRules?)
    val maximumLayer: Int
        get() = 0

    companion object {
        const val ORDER_YZX: Byte = 0
        const val ORDER_ZXY: Byte = 1
    }
}