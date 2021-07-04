package cn.nukkit.level.format.generic

import cn.nukkit.Nukkit

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
abstract class BaseLevelProvider : LevelProvider {
    protected var level: Level?

    @get:Override
    val path: String
    protected var levelData: CompoundTag? = null
    private var spawn: Vector3
    protected val lastRegion: AtomicReference<BaseRegionLoader> = AtomicReference()
    protected val regions: Long2ObjectMap<BaseRegionLoader> = Long2ObjectOpenHashMap()
    protected val chunks: Long2ObjectMap<BaseFullChunk> = Long2ObjectOpenHashMap()
    private val lastChunk: AtomicReference<BaseFullChunk> = AtomicReference()

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
    constructor(level: Level?, path: String) {
        this.level = level
        this.path = path
        val filePath = File(this.path)
        if (!filePath.exists() && !filePath.mkdirs()) {
            throw LevelException("Could not create the directory $filePath")
        }
        var levelData: CompoundTag
        val levelDatFile = File(path, "level.dat")
        try {
            FileInputStream(levelDatFile).use { fos ->
                BufferedInputStream(fos).use { input ->
                    levelData = NBTIO.readCompressed(input, ByteOrder.BIG_ENDIAN)
                }
            }
        } catch (e: Exception) {
            log.fatal("Failed to load the level.dat file at {}, attempting to load level.dat_old instead!", levelDatFile.getAbsolutePath(), e)
            try {
                val old = File(path, "level.dat_old")
                if (!old.isFile()) {
                    log.fatal("The file {} does not exists!", old.getAbsolutePath())
                    val ex = FileNotFoundException("The file " + old.getAbsolutePath().toString() + " does not exists!")
                    ex.addSuppressed(e)
                    throw ex
                }
                try {
                    FileInputStream(old).use { fos -> BufferedInputStream(fos).use { input -> levelData = NBTIO.readCompressed(input, ByteOrder.BIG_ENDIAN) } }
                } catch (e2: Exception) {
                    log.fatal("Failed to load the level.dat_old file at {}", levelDatFile.getAbsolutePath())
                    e2.addSuppressed(e)
                    throw e2
                }
            } catch (e2: Exception) {
                val ex = LevelException("Could not load the level.dat and the level.dat_old files. You might need to restore them from a backup!", e)
                ex.addSuppressed(e2)
                throw ex
            }
        }
        if (levelData.get("Data") is CompoundTag) {
            this.levelData = levelData.getCompound("Data")
        } else {
            throw LevelException("Invalid level.dat")
        }
        if (!this.levelData.contains("generatorName")) {
            this.levelData.putString("generatorName", Generator.getGenerator("DEFAULT").getSimpleName().toLowerCase())
        }
        if (!this.levelData.contains("generatorOptions")) {
            this.levelData.putString("generatorOptions", "")
        }
        this.levelData.putList(ListTag("ServerBrand").add(StringTag("", Nukkit.CODENAME)))
        spawn = Vector3(this.levelData.getInt("SpawnX"), this.levelData.getInt("SpawnY"), this.levelData.getInt("SpawnZ"))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(level: Level?, path: String, levelData: CompoundTag?, spawn: Vector3) {
        this.level = level
        this.path = path
        this.levelData = levelData
        this.spawn = spawn
    }

    abstract fun loadChunk(index: Long, chunkX: Int, chunkZ: Int, create: Boolean): BaseFullChunk?
    fun size(): Int {
        synchronized(chunks) { return chunks.size() }
    }

    @Override
    fun unloadChunks() {
        val iter: ObjectIterator<BaseFullChunk> = chunks.values().iterator()
        while (iter.hasNext()) {
            iter.next().unload(true, false)
            iter.remove()
        }
    }

    @get:Override
    val generator: String
        get() = levelData.getString("generatorName")

    @get:Override
    val generatorOptions: Map<String, Any>
        get() = object : HashMap<String?, Object?>() {
            init {
                put("preset", levelData.getString("generatorOptions"))
            }
        }

    @get:Override
    val loadedChunks: Map<Long, cn.nukkit.level.format.generic.BaseFullChunk>
        get() {
            synchronized(chunks) { return ImmutableMap.copyOf(chunks) }
        }

    @Override
    fun isChunkLoaded(X: Int, Z: Int): Boolean {
        return isChunkLoaded(Level.chunkHash(X, Z))
    }

    fun putChunk(index: Long, chunk: BaseFullChunk?) {
        synchronized(chunks) { chunks.put(index, chunk) }
    }

    @Override
    fun isChunkLoaded(hash: Long): Boolean {
        synchronized(chunks) { return chunks.containsKey(hash) }
    }

    fun getRegion(x: Int, z: Int): BaseRegionLoader {
        val index: Long = Level.chunkHash(x, z)
        synchronized(regions) { return regions.get(index) }
    }

    val server: Server
        get() = level.getServer()

    @Override
    fun getLevel(): Level? {
        return level
    }

    @get:Override
    val name: String
        get() = levelData.getString("LevelName")

    @get:Override
    @set:Override
    var isRaining: Boolean
        get() = levelData.getBoolean("raining")
        set(raining) {
            levelData.putBoolean("raining", raining)
        }

    @get:Override
    @set:Override
    var rainTime: Int
        get() = levelData.getInt("rainTime")
        set(rainTime) {
            levelData.putInt("rainTime", rainTime)
        }

    @get:Override
    @set:Override
    var isThundering: Boolean
        get() = levelData.getBoolean("thundering")
        set(thundering) {
            levelData.putBoolean("thundering", thundering)
        }

    @get:Override
    @set:Override
    var thunderTime: Int
        get() = levelData.getInt("thunderTime")
        set(thunderTime) {
            levelData.putInt("thunderTime", thunderTime)
        }

    @get:Override
    @set:Override
    var currentTick: Long
        get() = levelData.getLong("Time")
        set(currentTick) {
            levelData.putLong("Time", currentTick)
        }

    @get:Override
    @set:Override
    var time: Long
        get() = levelData.getLong("DayTime")
        set(value) {
            levelData.putLong("DayTime", value)
        }

    @get:Override
    @set:Override
    var seed: Long
        get() = levelData.getLong("RandomSeed")
        set(value) {
            levelData.putLong("RandomSeed", value)
        }

    @Override
    fun getSpawn(): Vector3 {
        return spawn
    }

    @Override
    fun setSpawn(pos: Vector3) {
        levelData.putInt("SpawnX", pos.x as Int)
        levelData.putInt("SpawnY", pos.y as Int)
        levelData.putInt("SpawnZ", pos.z as Int)
        spawn = pos
    }

    @get:Override
    val gamerules: GameRules
        get() {
            val rules: GameRules = GameRules.getDefault()
            if (levelData.contains("GameRules")) rules.readNBT(levelData.getCompound("GameRules"))
            return rules
        }

    @Override
    fun setGameRules(rules: GameRules) {
        levelData.putCompound("GameRules", rules.writeNBT())
    }

    @Override
    fun doGarbageCollection() {
        val limit = (System.currentTimeMillis() - 50) as Int
        synchronized(regions) {
            if (regions.isEmpty()) {
                return
            }
            val iter: ObjectIterator<BaseRegionLoader> = regions.values().iterator()
            while (iter.hasNext()) {
                val loader: BaseRegionLoader = iter.next()
                if (loader.lastUsed <= limit) {
                    try {
                        loader.close()
                    } catch (e: IOException) {
                        throw RuntimeException("Unable to close RegionLoader", e)
                    }
                    lastRegion.set(null)
                    iter.remove()
                }
            }
        }
    }

    @Override
    fun saveChunks() {
        synchronized(chunks) {
            for (chunk in chunks.values()) {
                if (chunk.getChanges() !== 0) {
                    chunk.setChanged(false)
                    this.saveChunk(chunk.getX(), chunk.getZ())
                }
            }
        }
    }

    fun getLevelData(): CompoundTag? {
        return levelData
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
    @Override
    fun saveLevelData() {
        val levelDataFile = File(path, "level.dat")
        try {
            Utils.safeWrite(levelDataFile) { file ->
                try {
                    FileOutputStream(file).use { fos -> BufferedOutputStream(fos).use { out -> NBTIO.writeGZIPCompressed(CompoundTag().putCompound("Data", levelData), out) } }
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
        } catch (e: IOException) {
            log.fatal("Failed to save the level.dat file at {}", levelDataFile.getAbsolutePath(), e)
            throw UncheckedIOException(e)
        }
    }

    fun updateLevelName(name: String?) {
        if (!this.name.equals(name)) {
            levelData.putString("LevelName", name)
        }
    }

    @Override
    fun loadChunk(chunkX: Int, chunkZ: Int): Boolean {
        return this.loadChunk(chunkX, chunkZ, false)
    }

    @Override
    fun loadChunk(chunkX: Int, chunkZ: Int, create: Boolean): Boolean {
        val index: Long = Level.chunkHash(chunkX, chunkZ)
        synchronized(chunks) {
            if (chunks.containsKey(index)) {
                return true
            }
        }
        return loadChunk(index, chunkX, chunkZ, create) != null
    }

    @Override
    fun unloadChunk(X: Int, Z: Int): Boolean {
        return this.unloadChunk(X, Z, true)
    }

    @Override
    fun unloadChunk(X: Int, Z: Int, safe: Boolean): Boolean {
        val index: Long = Level.chunkHash(X, Z)
        synchronized(chunks) {
            val chunk: BaseFullChunk = chunks.get(index)
            if (chunk != null && chunk.unload(false, safe)) {
                lastChunk.set(null)
                chunks.remove(index, chunk)
                return true
            }
        }
        return false
    }

    @Override
    fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk? {
        return this.getChunk(chunkX, chunkZ, false)
    }

    @Override
    fun getLoadedChunk(chunkX: Int, chunkZ: Int): BaseFullChunk? {
        var tmp: BaseFullChunk? = lastChunk.get()
        if (tmp != null && tmp.getX() === chunkX && tmp.getZ() === chunkZ) {
            return tmp
        }
        val index: Long = Level.chunkHash(chunkX, chunkZ)
        synchronized(chunks) { lastChunk.set(chunks.get(index).also { tmp = it }) }
        return tmp
    }

    @Override
    fun getLoadedChunk(hash: Long): BaseFullChunk? {
        var tmp: BaseFullChunk? = lastChunk.get()
        if (tmp != null && tmp.getIndex() === hash) {
            return tmp
        }
        synchronized(chunks) { lastChunk.set(chunks.get(hash).also { tmp = it }) }
        return tmp
    }

    @Override
    fun getChunk(chunkX: Int, chunkZ: Int, create: Boolean): BaseFullChunk? {
        var tmp: BaseFullChunk? = lastChunk.get()
        if (tmp != null && tmp.getX() === chunkX && tmp.getZ() === chunkZ) {
            return tmp
        }
        val index: Long = Level.chunkHash(chunkX, chunkZ)
        synchronized(chunks) { lastChunk.set(chunks.get(index).also { tmp = it }) }
        return if (tmp != null) {
            tmp
        } else {
            tmp = this.loadChunk(index, chunkX, chunkZ, create)
            lastChunk.set(tmp)
            tmp
        }
    }

    @Override
    fun setChunk(chunkX: Int, chunkZ: Int, chunk: FullChunk) {
        if (chunk !is BaseFullChunk) {
            throw ChunkException("Invalid Chunk class")
        }
        chunk.setProvider(this)
        chunk.setPosition(chunkX, chunkZ)
        val index: Long = Level.chunkHash(chunkX, chunkZ)
        synchronized(chunks) {
            if (chunks.containsKey(index) && !chunks.get(index).equals(chunk)) {
                this.unloadChunk(chunkX, chunkZ, false)
            }
            chunks.put(index, chunk as BaseFullChunk)
        }
    }

    @Override
    fun isChunkPopulated(chunkX: Int, chunkZ: Int): Boolean {
        val chunk: BaseFullChunk? = this.getChunk(chunkX, chunkZ)
        return chunk != null && chunk.isPopulated()
    }

    @Override
    @Synchronized
    fun close() {
        unloadChunks()
        synchronized(regions) {
            val iter: ObjectIterator<BaseRegionLoader> = regions.values().iterator()
            while (iter.hasNext()) {
                try {
                    iter.next().close()
                } catch (e: IOException) {
                    throw RuntimeException("Unable to close RegionLoader", e)
                }
                lastRegion.set(null)
                iter.remove()
            }
        }
        level = null
    }

    @Override
    fun isChunkGenerated(chunkX: Int, chunkZ: Int): Boolean {
        val region: BaseRegionLoader = getRegion(chunkX shr 5, chunkZ shr 5)
        return region != null && region.chunkExists(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32) && this.getChunk(chunkX - region.getX() * 32, chunkZ - region.getZ() * 32, true).isGenerated()
    }

    companion object {
        protected fun getRegionIndexX(chunkX: Int): Int {
            return chunkX shr 5
        }

        protected fun getRegionIndexZ(chunkZ: Int): Int {
            return chunkZ shr 5
        }
    }
}