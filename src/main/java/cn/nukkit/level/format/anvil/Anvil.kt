package cn.nukkit.level.format.anvil

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Anvil(level: Level?, path: String?) : BaseLevelProvider(level, path) {
    @Override
    fun getEmptyChunk(chunkX: Int, chunkZ: Int): Chunk? {
        return Chunk.getEmptyChunk(chunkX, chunkZ, this)
    }

    @Override
    @Throws(ChunkException::class)
    fun requestChunkTask(x: Int, z: Int): AsyncTask? {
        val chunk: Chunk = this.getChunk(x, z, false) as Chunk? ?: throw ChunkException("Invalid Chunk Set")
        val timestamp: Long = chunk.getChanges()
        var blockEntities: ByteArray = EmptyArrays.EMPTY_BYTES
        if (!chunk.getBlockEntities().isEmpty()) {
            val tagList: List<CompoundTag> = ArrayList()
            for (blockEntity in chunk.getBlockEntities().values()) {
                if (blockEntity is BlockEntitySpawnable) {
                    tagList.add((blockEntity as BlockEntitySpawnable).getSpawnCompound())
                }
            }
            blockEntities = try {
                NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        val stream: BinaryStream = ThreadCache.binaryStream.get().reset()
        var count = 0
        val sections: Array<cn.nukkit.level.format.ChunkSection> = chunk.getSections()
        for (i in sections.indices.reversed()) {
            if (!sections[i].isEmpty()) {
                count = i + 1
                break
            }
        }
        for (i in 0 until count) {
            sections[i].writeTo(stream)
        }
        stream.put(chunk.getBiomeIdArray())
        stream.putByte(0.toByte()) // Border blocks
        stream.put(blockEntities)
        this.getLevel().chunkRequestCallback(timestamp, x, z, count, stream.getBuffer())
        return null
    }

    private var lastPosition = 0
    @Override
    fun doGarbageCollection(time: Long) {
        val start: Long = System.currentTimeMillis()
        val maxIterations: Int = size()
        if (lastPosition > maxIterations) lastPosition = 0
        var i: Int
        synchronized(chunks) {
            var iter: ObjectIterator<BaseFullChunk?> = chunks.values().iterator()
            if (lastPosition != 0) iter.skip(lastPosition)
            i = 0
            while (i < maxIterations) {
                if (!iter.hasNext()) {
                    iter = chunks.values().iterator()
                }
                if (!iter.hasNext()) break
                val chunk: BaseFullChunk = iter.next()
                if (chunk == null) {
                    i++
                    continue
                }
                if (chunk.isGenerated() && chunk.isPopulated() && chunk is Chunk) {
                    val anvilChunk: Chunk = chunk
                    chunk.compress()
                    if (System.currentTimeMillis() - start >= time) break
                }
                i++
            }
        }
        lastPosition += i
    }

    @Override
    @Synchronized
    fun loadChunk(index: Long, chunkX: Int, chunkZ: Int, create: Boolean): BaseFullChunk? {
        val regionX: Int = getRegionIndexX(chunkX)
        val regionZ: Int = getRegionIndexZ(chunkZ)
        val region: BaseRegionLoader? = loadRegion(regionX, regionZ)
        this.level.timings.syncChunkLoadDataTimer.startTiming()
        var chunk: BaseFullChunk?
        chunk = try {
            region.readChunk(chunkX - regionX * 32, chunkZ - regionZ * 32)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        if (chunk == null) {
            if (create) {
                chunk = getEmptyChunk(chunkX, chunkZ)
                putChunk(index, chunk)
            }
        } else {
            putChunk(index, chunk)
        }
        this.level.timings.syncChunkLoadDataTimer.stopTiming()
        return chunk
    }

    @Override
    @Synchronized
    fun saveChunk(X: Int, Z: Int) {
        val chunk: BaseFullChunk = this.getChunk(X, Z)
        if (chunk != null) {
            try {
                loadRegion(X shr 5, Z shr 5).writeChunk(chunk)
            } catch (e: Exception) {
                throw ChunkException("Error saving chunk ($X, $Z)", e)
            }
        }
    }

    @Override
    @Synchronized
    fun saveChunk(x: Int, z: Int, chunk: FullChunk) {
        if (chunk !is Chunk) {
            throw ChunkException("Invalid Chunk class")
        }
        val regionX = x shr 5
        val regionZ = z shr 5
        loadRegion(regionX, regionZ)
        chunk.setX(x)
        chunk.setZ(z)
        try {
            this.getRegion(regionX, regionZ).writeChunk(chunk)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Synchronized
    protected fun loadRegion(x: Int, z: Int): BaseRegionLoader? {
        val tmp: BaseRegionLoader = lastRegion.get()
        if (tmp != null && x == tmp.getX() && z == tmp.getZ()) {
            return tmp
        }
        val index: Long = Level.chunkHash(x, z)
        synchronized(regions) {
            var region: BaseRegionLoader? = this.regions.get(index)
            if (region == null) {
                try {
                    region = RegionLoader(this, x, z)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
                this.regions.put(index, region)
            }
            lastRegion.set(region)
            return region
        }
    }

    @get:Override
    val maximumLayer: Int
        get() = 1

    companion object {
        const val VERSION = 19133
        private val PAD_256 = ByteArray(256)
        val providerName: String
            get() = "anvil"
        val providerOrder: Byte
            get() = ORDER_YZX

        fun usesChunkSection(): Boolean {
            return true
        }

        fun isValid(path: String): Boolean {
            var isValid = File("$path/level.dat").exists() && File("$path/region/").isDirectory()
            if (isValid) {
                for (file in File("$path/region/").listFiles { dir, name -> Pattern.matches("^.+\\.mc[r|a]$", name) }) {
                    if (!file.getName().endsWith(".mca")) {
                        isValid = false
                        break
                    }
                }
            }
            return isValid
        }

        @Throws(IOException::class)
        fun generate(path: String, name: String?, seed: Long, generator: Class<out Generator?>?) {
            generate(path, name, seed, generator, HashMap())
        }

        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
        @Throws(IOException::class)
        fun generate(path: String, name: String?, seed: Long, generator: Class<out Generator?>?, options: Map<String?, String?>) {
            val regionDir = File("$path/region")
            if (!regionDir.exists() && !regionDir.mkdirs()) {
                throw IOException("Could not create the directory $regionDir")
            }
            val levelData: CompoundTag = CompoundTag("Data")
                    .putCompound("GameRules", CompoundTag())
                    .putLong("DayTime", 0)
                    .putInt("GameType", 0)
                    .putString("generatorName", Generator.getGeneratorName(generator))
                    .putString("generatorOptions", options.getOrDefault("preset", ""))
                    .putInt("generatorVersion", 1)
                    .putBoolean("hardcore", false)
                    .putBoolean("initialized", true)
                    .putLong("LastPlayed", System.currentTimeMillis() / 1000)
                    .putString("LevelName", name)
                    .putBoolean("raining", false)
                    .putInt("rainTime", 0)
                    .putLong("RandomSeed", seed)
                    .putInt("SpawnX", 128)
                    .putInt("SpawnY", 70)
                    .putInt("SpawnZ", 128)
                    .putBoolean("thundering", false)
                    .putInt("thunderTime", 0)
                    .putInt("version", VERSION)
                    .putLong("Time", 0)
                    .putLong("SizeOnDisk", 0)
            Utils.safeWrite(File(path, "level.dat")) { file ->
                try {
                    FileOutputStream(file).use { fos -> BufferedOutputStream(fos).use { out -> NBTIO.writeGZIPCompressed(CompoundTag().putCompound("Data", levelData), out, ByteOrder.BIG_ENDIAN) } }
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
        }

        fun createChunkSection(y: Int): ChunkSection {
            val cs = ChunkSection(y)
            cs.hasSkyLight = true
            return cs
        }
    }
}