package cn.nukkit.level.format.generic

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BaseRegionLoader(level: LevelProvider?, regionX: Int, regionZ: Int, ext: String) {
    protected var x = 0
    protected var z = 0
    protected var lastSector = 0
    protected var levelProvider: LevelProvider? = null
    private var randomAccessFile: RandomAccessFile? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected val primitiveLocationTable: Int2ObjectMap<IntArray> = Int2ObjectOpenHashMap()

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Integer boxing was polluting the memory heap", replaceWith = "primitiveLocationTable")
    protected val locationTable: Map<Integer, Array<Integer>> = ConvertingMapWrapper(
            primitiveLocationTable,
            { table -> Arrays.stream(table).mapToInt(Integer::intValue).toArray() }
    ) { table -> Arrays.stream(table).boxed().toArray { _Dummy_.__Array__() } }
    var lastUsed: Long = 0
    fun compress() {
        // TODO
    }

    fun getRandomAccessFile(): RandomAccessFile? {
        return randomAccessFile
    }

    protected abstract fun isChunkGenerated(index: Int): Boolean
    @Throws(IOException::class)
    abstract fun readChunk(x: Int, z: Int): BaseFullChunk?
    protected abstract fun unserializeChunk(data: ByteArray?): BaseFullChunk?
    abstract fun chunkExists(x: Int, z: Int): Boolean
    @Throws(IOException::class)
    protected abstract fun saveChunk(x: Int, z: Int, chunkData: ByteArray?)
    abstract fun removeChunk(x: Int, z: Int)
    @Throws(Exception::class)
    abstract fun writeChunk(chunk: FullChunk?)
    @Throws(IOException::class)
    fun close() {
        if (randomAccessFile != null) randomAccessFile.close()
    }

    @Throws(IOException::class)
    protected abstract fun loadLocationTable()
    @Throws(Exception::class)
    abstract fun doSlowCleanUp(): Int
    @Throws(IOException::class)
    protected abstract fun writeLocationIndex(index: Int)
    @Throws(IOException::class)
    protected abstract fun createBlank()
    abstract fun getX(): Int
    abstract fun getZ(): Int

    @get:DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Unnecessary int-boxing causing heap pollution", replaceWith = "getIntLocationIndexes()")
    @get:Deprecated
    val locationIndexes: Array<Any>
        get() = primitiveLocationTable.keySet().toArray(Utils.EMPTY_INTEGERS)

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val intLocationIndexes: IntArray
        get() = primitiveLocationTable.keySet().toIntArray()

    companion object {
        const val VERSION = 1
        const val COMPRESSION_GZIP: Byte = 1
        const val COMPRESSION_ZLIB: Byte = 2
        const val MAX_SECTOR_LENGTH = 256 shl 12
        const val COMPRESSION_LEVEL = 7
    }

    init {
        try {
            x = regionX
            z = regionZ
            levelProvider = level
            val filePath: String = levelProvider.getPath().toString() + "region/r." + regionX + "." + regionZ + "." + ext
            val file = File(filePath)
            val exists: Boolean = file.exists()
            if (!exists) {
                file.createNewFile()
            }
            // TODO: buffering is a temporary solution to chunk reading/writing being poorly optimized
            //  - need to fix the code where it reads single bytes at a time from disk
            randomAccessFile = RandomAccessFile(filePath, "rw")
            if (!exists) {
                createBlank()
            } else {
                loadLocationTable()
            }
            lastUsed = System.currentTimeMillis()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}