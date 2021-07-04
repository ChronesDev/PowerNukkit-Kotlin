package cn.nukkit.level.format.anvil

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class Chunk(level: LevelProvider, nbt: CompoundTag?) : BaseChunk() {
    protected var inhabitedTime: Long
    protected var terrainPopulated: Boolean
    protected var terrainGenerated: Boolean
    @Override
    fun clone(): Chunk {
        return super.clone() as Chunk
    }

    constructor(level: LevelProvider?) : this(level, null) {}
    constructor(providerClass: Class<out LevelProvider?>) : this(null as LevelProvider?, null) {
        providerClass = providerClass
    }

    constructor(providerClass: Class<out LevelProvider?>, nbt: CompoundTag?) : this(null as LevelProvider?, nbt) {
        providerClass = providerClass
    }

    @get:Override
    @set:Override
    var isPopulated: Boolean
        get() = terrainPopulated
        set(value) {
            if (value != terrainPopulated) {
                terrainPopulated = value
                setChanged()
            }
        }

    @Override
    fun setPopulated() {
        isPopulated = true
    }

    @get:Override
    @set:Override
    var isGenerated: Boolean
        get() = terrainGenerated || terrainPopulated
        set(value) {
            if (terrainGenerated != value) {
                terrainGenerated = value
                setChanged()
            }
        }

    @Override
    fun setGenerated() {
        isGenerated = true
    }

    val nBT: CompoundTag
        get() {
            val tag = CompoundTag()
            tag.put("LightPopulated", ByteTag("LightPopulated", (if (isLightPopulated()) 1 else 0).toByte()))
            tag.put("InhabitedTime", LongTag("InhabitedTime", inhabitedTime))
            tag.put("V", ByteTag("V", 1.toByte()))
            tag.put("TerrainGenerated", ByteTag("TerrainGenerated", (if (isGenerated) 1 else 0).toByte()))
            tag.put("TerrainPopulated", ByteTag("TerrainPopulated", (if (isPopulated) 1 else 0).toByte()))
            return tag
        }

    @Override
    fun toFastBinary(): ByteArray {
        val nbt: CompoundTag = nBT.copy()
        nbt.remove("BiomeColors")
        nbt.putInt("xPos", this.getX())
        nbt.putInt("zPos", this.getZ())
        nbt.putByteArray("Biomes", this.getBiomeIdArray())
        val heightInts = IntArray(256)
        val heightBytes: ByteArray = this.getHeightMapArray()
        for (i in heightInts.indices) {
            heightInts[i] = heightBytes[i] and 0xFF
        }
        for (section in this.getSections()) {
            if (section is EmptyChunkSection) {
                continue
            }
            val s: CompoundTag = section.toNBT()
            nbt.getList("Sections", CompoundTag::class.java).add(s)
        }
        val entities: ArrayList<CompoundTag> = ArrayList()
        for (entity in this.getEntities().values()) {
            if (entity !is Player && !entity.closed) {
                entity.saveNBT()
                entities.add(entity.namedTag)
            }
        }
        val entityListTag: ListTag<CompoundTag> = ListTag("Entities")
        entityListTag.setAll(entities)
        nbt.putList(entityListTag)
        val tiles: ArrayList<CompoundTag> = ArrayList()
        for (blockEntity in this.getBlockEntities().values()) {
            blockEntity.saveNBT()
            tiles.add(blockEntity.namedTag)
        }
        val tileListTag: ListTag<CompoundTag> = ListTag("TileEntities")
        tileListTag.setAll(tiles)
        nbt.putList(tileListTag)
        val entries: Set<BlockUpdateEntry> = this.provider.getLevel().getPendingBlockUpdates(this)
        if (entries != null) {
            val tileTickTag: ListTag<CompoundTag> = ListTag("TileTicks")
            val totalTime: Long = this.provider.getLevel().getCurrentTick()
            for (entry in entries) {
                val entryNBT: CompoundTag = CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (entry.delay - totalTime) as Int)
                        .putInt("p", entry.priority)
                tileTickTag.add(entryNBT)
            }
            nbt.putList(tileTickTag)
        }
        val extraData = BinaryStream()
        val extraDataArray: Map<Integer, Integer> = this.getBlockExtraDataArray()
        extraData.putInt(extraDataArray.size())
        for (key in extraDataArray.keySet()) {
            extraData.putInt(key)
            extraData.putShort(extraDataArray[key])
        }
        nbt.putByteArray("ExtraData", extraData.getBuffer())
        val chunk = CompoundTag("")
        chunk.putCompound("Level", nbt)
        return try {
            NBTIO.write(chunk, ByteOrder.BIG_ENDIAN)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    @Override
    fun toBinary(): ByteArray {
        val nbt: CompoundTag = nBT.copy()
        nbt.remove("BiomeColors")
        nbt.putInt("xPos", this.getX())
        nbt.putInt("zPos", this.getZ())
        val sectionList: ListTag<CompoundTag> = ListTag("Sections")
        for (section in this.getSections()) {
            if (section is EmptyChunkSection) {
                continue
            }
            val s: CompoundTag = section.toNBT()
            if (!section.hasBlocks()) {
                continue
            }
            sectionList.add(s)
        }
        nbt.putList(sectionList)
        nbt.putByteArray("Biomes", this.getBiomeIdArray())
        val heightInts = IntArray(256)
        val heightBytes: ByteArray = this.getHeightMapArray()
        for (i in heightInts.indices) {
            heightInts[i] = heightBytes[i] and 0xFF
        }
        nbt.putIntArray("HeightMap", heightInts)
        val entities: ArrayList<CompoundTag> = ArrayList()
        for (entity in this.getEntities().values()) {
            if (entity !is Player && !entity.closed) {
                entity.saveNBT()
                entities.add(entity.namedTag)
            }
        }
        val entityListTag: ListTag<CompoundTag> = ListTag("Entities")
        entityListTag.setAll(entities)
        nbt.putList(entityListTag)
        val tiles: ArrayList<CompoundTag> = ArrayList()
        for (blockEntity in this.getBlockEntities().values()) {
            blockEntity.saveNBT()
            tiles.add(blockEntity.namedTag)
        }
        val tileListTag: ListTag<CompoundTag> = ListTag("TileEntities")
        tileListTag.setAll(tiles)
        nbt.putList(tileListTag)
        var entries: Set<BlockUpdateEntry>? = null
        if (this.provider != null) {
            val level: Level = provider.getLevel()
            if (level != null) {
                entries = level.getPendingBlockUpdates(this)
            }
        }
        if (entries != null) {
            val tileTickTag: ListTag<CompoundTag> = ListTag("TileTicks")
            val totalTime: Long = this.provider.getLevel().getCurrentTick()
            for (entry in entries) {
                val entryNBT: CompoundTag = CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (entry.delay - totalTime) as Int)
                        .putInt("p", entry.priority)
                tileTickTag.add(entryNBT)
            }
            nbt.putList(tileTickTag)
        }
        val extraData = BinaryStream()
        val extraDataArray: Map<Integer, Integer> = this.getBlockExtraDataArray()
        extraData.putInt(extraDataArray.size())
        for (key in extraDataArray.keySet()) {
            extraData.putInt(key)
            extraData.putShort(extraDataArray[key])
        }
        nbt.putByteArray("ExtraData", extraData.getBuffer())
        val chunk = CompoundTag("")
        chunk.putCompound("Level", nbt)
        return try {
            Zlib.deflate(NBTIO.write(chunk, ByteOrder.BIG_ENDIAN), RegionLoader.COMPRESSION_LEVEL)
        } catch (e: IOException) {
            throw UncheckedIOException(e)
        }
    }

    @Override
    fun getBlockSkyLight(x: Int, y: Int, z: Int): Int {
        val section: cn.nukkit.level.format.ChunkSection = this.sections.get(y shr 4)
        return if (section is cn.nukkit.level.format.anvil.ChunkSection) {
            val anvilSection: cn.nukkit.level.format.anvil.ChunkSection = section
            if (anvilSection.skyLight != null) {
                section.getBlockSkyLight(x, y and 0x0f, z)
            } else if (!anvilSection.hasSkyLight) {
                0
            } else {
                val height: Int = getHighestBlockAt(x, z)
                if (height < y) {
                    15
                } else if (height == y) {
                    if (Block.transparent.get(getBlockId(x, y, z))) 15 else 0
                } else {
                    section.getBlockSkyLight(x, y and 0x0f, z)
                }
            }
        } else {
            section.getBlockSkyLight(x, y and 0x0f, z)
        }
    }

    @Override
    fun getBlockLight(x: Int, y: Int, z: Int): Int {
        val section: cn.nukkit.level.format.ChunkSection = this.sections.get(y shr 4)
        return if (section is cn.nukkit.level.format.anvil.ChunkSection) {
            val anvilSection: cn.nukkit.level.format.anvil.ChunkSection = section
            if (anvilSection.blockLight != null) {
                section.getBlockLight(x, y and 0x0f, z)
            } else if (!anvilSection.hasBlockLight) {
                0
            } else {
                section.getBlockLight(x, y and 0x0f, z)
            }
        } else {
            section.getBlockLight(x, y and 0x0f, z)
        }
    }

    @Override
    fun compress(): Boolean {
        super.compress()
        var result = false
        for (section in getSections()) {
            if (section is ChunkSection) {
                val anvilSection: ChunkSection = section
                if (!anvilSection.isEmpty()) {
                    result = result or anvilSection.compress()
                }
            }
        }
        return result
    }

    companion object {
        @JvmOverloads
        fun fromBinary(data: ByteArray?, provider: LevelProvider? = null): Chunk? {
            return try {
                val chunk: CompoundTag = NBTIO.read(ByteArrayInputStream(Zlib.inflate(data)), ByteOrder.BIG_ENDIAN)
                if (!chunk.contains("Level") || chunk.get("Level") !is CompoundTag) {
                    null
                } else Chunk(provider, chunk.getCompound("Level"))
            } catch (e: Exception) {
                log.error("An error has occurred while parsing a chunk from {}", provider.getName(), e)
                null
            }
        }

        @JvmOverloads
        fun fromFastBinary(data: ByteArray?, provider: LevelProvider? = null): Chunk? {
            return try {
                val chunk: CompoundTag = NBTIO.read(DataInputStream(ByteArrayInputStream(data)), ByteOrder.BIG_ENDIAN)
                if (!chunk.contains("Level") || chunk.get("Level") !is CompoundTag) {
                    null
                } else Chunk(provider, chunk.getCompound("Level"))
            } catch (e: Exception) {
                null
            }
        }

        fun getEmptyChunk(chunkX: Int, chunkZ: Int): Chunk? {
            return getEmptyChunk(chunkX, chunkZ, null)
        }

        fun getEmptyChunk(chunkX: Int, chunkZ: Int, provider: LevelProvider?): Chunk? {
            return try {
                val chunk: Chunk
                if (provider != null) {
                    chunk = Chunk(provider, null)
                } else {
                    chunk = Chunk(Anvil::class.java, null)
                }
                chunk.setPosition(chunkX, chunkZ)
                chunk.heightMap = ByteArray(256)
                chunk.inhabitedTime = 0
                chunk.terrainGenerated = false
                chunk.terrainPopulated = false
                //            chunk.lightPopulated = false;
                chunk
            } catch (e: Exception) {
                null
            }
        }
    }

    init {
        this.provider = level
        if (level != null) {
            this.providerClass = level.getClass()
        }
        this.sections = arrayOfNulls<cn.nukkit.level.format.ChunkSection>(16)
        System.arraycopy(EmptyChunkSection.EMPTY, 0, this.sections, 0, 16)
        if (nbt == null) {
            this.biomes = ByteArray(16 * 16)
            this.heightMap = ByteArray(256)
            this.NBTentities = ArrayList(0)
            this.NBTtiles = ArrayList(0)
            return
        }
        for (section in nbt.getList("Sections").getAll()) {
            if (section is CompoundTag) {
                val y: Int = (section as CompoundTag).getByte("Y")
                if (y < 16) {
                    val chunkSection = ChunkSection(section as CompoundTag)
                    if (chunkSection.hasBlocks()) {
                        sections.get(y) = chunkSection
                    } else {
                        sections.get(y) = EmptyChunkSection.EMPTY.get(y)
                    }
                }
            }
        }
        var extraData: Map<Integer, Integer> = HashMap()
        val extra: Tag = nbt.get("ExtraData")
        if (extra is ByteArrayTag) {
            val stream = BinaryStream((extra as ByteArrayTag).data)
            for (i in 0 until stream.getInt()) {
                val key: Int = stream.getInt()
                extraData.put(key, stream.getShort())
            }
        }
        this.setPosition(nbt.getInt("xPos"), nbt.getInt("zPos"))
        if (sections.length > SECTION_COUNT) {
            throw ChunkException("Invalid amount of chunks")
        }
        if (nbt.contains("BiomeColors")) {
            this.biomes = ByteArray(16 * 16)
            val biomeColors: IntArray = nbt.getIntArray("BiomeColors")
            if (biomeColors != null && biomeColors.size == 256) {
                val palette = BiomePalette(biomeColors)
                for (x in 0..15) {
                    for (z in 0..15) {
                        this.biomes.get(x shl 4 or z) = (palette.get(x, z) shr 24)
                    }
                }
            }
        } else {
            this.biomes = Arrays.copyOf(nbt.getByteArray("Biomes"), 256)
        }
        var heightMap: IntArray = nbt.getIntArray("HeightMap")
        heightMap = ByteArray(256)
        if (heightMap.size != 256) {
            Arrays.fill(heightMap, 255.toByte())
        } else {
            for (i in heightMap.indices) {
                heightMap.get(i) = heightMap[i].toByte()
            }
        }
        if (!extraData.isEmpty()) extraData = extraData
        this.NBTentities = nbt.getList("Entities", CompoundTag::class.java).getAll()
        this.NBTtiles = nbt.getList("TileEntities", CompoundTag::class.java).getAll()
        if (this.NBTentities.isEmpty()) this.NBTentities = null
        if (this.NBTtiles.isEmpty()) this.NBTtiles = null
        val updateEntries: ListTag<CompoundTag> = nbt.getList("TileTicks", CompoundTag::class.java)
        if (updateEntries != null && updateEntries.size() > 0) {
            for (entryNBT in updateEntries.getAll()) {
                var block: Block? = null
                try {
                    val tag: Tag = entryNBT.get("i")
                    if (tag is StringTag) {
                        val name: String = (tag as StringTag).data
                        @SuppressWarnings("unchecked") val clazz: Class<out Block?> = Class.forName("cn.nukkit.block.$name") as Class<out Block?>
                        val constructor: Constructor = clazz.getDeclaredConstructor()
                        constructor.setAccessible(true)
                        block = constructor.newInstance() as Block
                    }
                } catch (e: Throwable) {
                    continue
                }
                if (block == null) {
                    continue
                }
                block.x = entryNBT.getInt("x")
                block.y = entryNBT.getInt("y")
                block.z = entryNBT.getInt("z")
                block.layer = 0
                this.provider.getLevel().scheduleUpdate(block, block, entryNBT.getInt("t"), entryNBT.getInt("p"), false)
            }
        }
        inhabitedTime = nbt.getLong("InhabitedTime")
        terrainPopulated = nbt.getBoolean("TerrainPopulated")
        terrainGenerated = nbt.getBoolean("TerrainGenerated")
    }
}