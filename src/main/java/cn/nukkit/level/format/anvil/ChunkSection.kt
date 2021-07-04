package cn.nukkit.level.format.anvil

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
@SuppressWarnings("java:S2176")
@ParametersAreNonnullByDefault
class ChunkSection : cn.nukkit.level.format.ChunkSection {
    @get:Override
    override val y: Int
    private var layerStorage: LayerStorage? = null
    var blockLight: ByteArray?
    var skyLight: ByteArray?
    protected var compressedLight: ByteArray?
    var hasBlockLight = false
    var hasSkyLight = false

    @get:Override
    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly("Needed for level backward compatibility")
    @set:Override
    @set:Since("1.3.1.0-PN")
    @set:PowerNukkitOnly("Needed for level backward compatibility")
    override var contentVersion = 0

    private constructor(
            y: Int, layerStorage: LayerStorage, @Nullable blockLight: ByteArray?, @Nullable skyLight: ByteArray?,
            @Nullable compressedLight: ByteArray?, hasBlockLight: Boolean, hasSkyLight: Boolean) {
        this.y = y
        this.layerStorage = layerStorage
        this.skyLight = skyLight
        this.blockLight = blockLight
        this.compressedLight = compressedLight
        this.hasBlockLight = hasBlockLight
        this.hasSkyLight = hasSkyLight
    }

    constructor(y: Int) {
        this.y = y
        contentVersion = ChunkUpdater.getCurrentContentVersion()
        layerStorage = LayerStorage.EMPTY
        hasBlockLight = false
        hasSkyLight = false
    }

    constructor(nbt: CompoundTag) {
        y = nbt.getByte("Y")
        contentVersion = nbt.getByte("ContentVersion")
        val version: Int = nbt.getByte("Version")
        val storageTagList: ListTag<CompoundTag> = getStorageTagList(nbt, version)
        when (storageTagList.size()) {
            0 -> layerStorage = LayerStorage.EMPTY
            1 -> layerStorage = SingleLayerStorage()
            else -> layerStorage = MultiLayerStorage(ImmutableBlockStorage.EMPTY, ImmutableBlockStorage.EMPTY)
        }
        for (i in 0 until storageTagList.size()) {
            val storageTag: CompoundTag = storageTagList.get(i)
            loadStorage(i, storageTag)
        }
        layerStorage.compress { storage: LayerStorage -> setLayerStorage(storage) }
        blockLight = nbt.getByteArray("BlockLight")
        skyLight = nbt.getByteArray("SkyLight")
    }

    private fun loadStorage(layer: Int, storageTag: CompoundTag) {
        var blocks: ByteArray = storageTag.getByteArray("Blocks")
        var hasBlockIds = false
        if (blocks.size == 0) {
            blocks = EmptyChunkSection.EMPTY_ID_ARRAY
        } else {
            hasBlockIds = true
        }
        var blocksExtra: ByteArray = storageTag.getByteArray("BlocksExtra")
        if (blocksExtra.size == 0) {
            blocksExtra = EmptyChunkSection.EMPTY_ID_ARRAY
        }
        var dataBytes: ByteArray = storageTag.getByteArray("Data")
        if (dataBytes.size == 0) {
            dataBytes = EmptyChunkSection.EMPTY_DATA_ARRAY
        } else {
            hasBlockIds = true
        }
        val data = NibbleArray(dataBytes)
        var dataExtraBytes: ByteArray = storageTag.getByteArray("DataExtra")
        if (dataExtraBytes.size == 0) {
            dataExtraBytes = EmptyChunkSection.EMPTY_DATA_ARRAY
        }
        val dataExtra = NibbleArray(dataExtraBytes)
        val hugeDataList: ListTag<ByteArrayTag> = storageTag.getList(HUGE_TAG_NAME, ByteArrayTag::class.java)
        val hugeDataSize: Int = hugeDataList.size()
        if (!hasBlockIds && hugeDataSize == 0) {
            return
        }
        if (contentVersion > ChunkUpdater.getCurrentContentVersion()) {
            log.warn(
                    "Loading a chunk section with content version ({}) higher than the current version ({}), " +
                            "Errors may occur and the chunk may get corrupted blocks!",
                    contentVersion, ChunkUpdater.getCurrentContentVersion()
            )
        }
        val storage: BlockStorage = layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer)

        // Convert YZX to XZY
        for (bx in 0..15) {
            for (bz in 0..15) {
                for (by in 0..15) {
                    val index = getAnvilIndex(bx, by, bz)
                    val blockId = composeBlockId(blocks[index], blocksExtra[index])
                    val composedData = composeData(data.get(index), dataExtra.get(index))
                    val state: BlockState = loadState(index, blockId, composedData, hugeDataList, hugeDataSize)
                    storage.setBlockState(bx, by, bz, state)
                }
            }
        }
    }

    @Override
    override fun getBlockId(x: Int, y: Int, z: Int): Int {
        return getBlockId(x, y, z, 0)
    }

    @Override
    override fun getBlockId(x: Int, y: Int, z: Int, layer: Int): Int {
        return layerStorage!!.getStorageOrEmpty(layer).getBlockId(x, y, z)
    }

    @Override
    override fun setBlockId(x: Int, y: Int, z: Int, id: Int) {
        setBlockId(x, y, z, 0, id)
    }

    @Override
    @Synchronized
    override fun setBlockId(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        if (id != 0) {
            layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer).setBlockId(x, y, z, id)
        } else {
            val storage: BlockStorage = layerStorage!!.getStorageOrNull(layer)
            if (storage != null) {
                storage.setBlockId(x, y, z, id)
            }
        }
    }

    private fun setLayerStorage(storage: LayerStorage) {
        layerStorage = storage
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    override fun setFullBlockId(x: Int, y: Int, z: Int, fullId: Int): Boolean {
        setFullBlockId(x, y, z, 0, fullId)
        return true
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setFullBlockId(x: Int, y: Int, z: Int, layer: Int, fullId: Int): Boolean {
        if (fullId != 0) {
            layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer).setFullBlock(x, y, z, fullId)
        } else {
            val storage: BlockStorage = layerStorage!!.getStorageOrNull(layer)
            if (storage != null) {
                storage.setFullBlock(x, y, z, fullId)
            }
        }
        return true
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun getBlockData(x: Int, y: Int, z: Int): Int {
        return getBlockData(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun getBlockData(x: Int, y: Int, z: Int, layer: Int): Int {
        return layerStorage!!.getStorageOrEmpty(layer).getBlockData(x, y, z)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun setBlockData(x: Int, y: Int, z: Int, data: Int) {
        setBlockData(x, y, z, 0, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    @Synchronized
    override fun setBlockData(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        if (data != 0) {
            layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer).setBlockData(x, y, z, data)
        } else {
            val storage: BlockStorage = layerStorage!!.getStorageOrNull(layer)
            if (storage != null) {
                storage.setBlockData(x, y, z, data)
            }
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return getFullBlock(x, y, z, 0)
    }

    @Nonnull
    @Override
    override fun getBlockState(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return layerStorage!!.getStorageOrEmpty(layer).getBlockState(x, y, z)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int {
        return layerStorage!!.getStorageOrEmpty(layer).getFullBlock(x, y, z)
    }

    @Override
    override fun setBlock(x: Int, y: Int, z: Int, blockId: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, 0, BlockState.of(blockId))
    }

    @Override
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId))
    }

    @Nonnull
    @Override
    override fun getAndSetBlock(x: Int, y: Int, z: Int, block: Block): Block {
        return getAndSetBlock(x, y, z, 0, block)
    }

    @Nonnull
    @Override
    @Synchronized
    override fun getAndSetBlock(x: Int, y: Int, z: Int, layer: Int, block: Block): Block {
        val storage: BlockStorage
        if (block.getId() !== 0 || !block.isDefaultState()) {
            storage = layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer)
        } else {
            storage = layerStorage!!.getStorageOrNull(layer)
            if (storage == null) {
                return BlockState.AIR.getBlock()
            }
        }
        val state: BlockState = storage.getAndSetBlockState(x, y, z, block.getCurrentState())
        return try {
            state.getBlock()
        } catch (ignored: InvalidBlockStateException) {
            BlockUnknown(state.getBlockId(), state.getExactIntStorage())
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    @Synchronized
    override fun getAndSetBlockState(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): BlockState {
        return if (!BlockState.AIR.equals(state)) {
            layerStorage!!.getOrSetStorage({ storage: LayerStorage -> setLayerStorage(storage) }, { contentVersion }, layer).getAndSetBlockState(x, y, z, state)
        } else {
            val storage: BlockStorage = layerStorage!!.getStorageOrNull(layer) ?: return BlockState.AIR
            storage.getAndSetBlockState(x, y, z, state)
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun setBlock(x: Int, y: Int, z: Int, blockId: Int, meta: Int): Boolean {
        return setBlockAtLayer(x, y, z, 0, blockId, meta)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta))
    }

    @Override
    @Synchronized
    override fun setBlockStateAtLayer(x: Int, y: Int, z: Int, layer: Int, state: BlockState): Boolean {
        val previous: BlockState = getAndSetBlockState(x, y, z, layer, state)
        return !state.equals(previous)
    }

    @Override
    override fun getBlockChangeStateAbove(x: Int, y: Int, z: Int): Int {
        val storage: BlockStorage = layerStorage!!.getStorageOrNull(0) ?: return 0
        return storage.getBlockChangeStateAbove(x, y, z)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @Synchronized
    override fun delayPaletteUpdates() {
        layerStorage!!.delayPaletteUpdates()
    }

    @Override
    override fun getBlockSkyLight(x: Int, y: Int, z: Int): Int {
        if (skyLight == null) {
            if (!hasSkyLight) {
                return 0
            } else if (compressedLight == null) {
                return 15
            }
        }
        skyLight = skyLightArray
        val sl: Int = skyLight!![y shl 7 or (z shl 3) or (x shr 1)] and 0xff
        return if (x and 1 == 0) {
            sl and 0x0f
        } else sl shr 4
    }

    @Override
    override fun setBlockSkyLight(x: Int, y: Int, z: Int, level: Int) {
        if (skyLight == null) {
            if (hasSkyLight && compressedLight != null) {
                skyLight = skyLightArray
            } else if (level == (if (hasSkyLight) 15 else 0)) {
                return
            } else {
                skyLight = ByteArray(2048)
                if (hasSkyLight) {
                    Arrays.fill(skyLight, 0xFF.toByte())
                }
            }
        }
        val i = y shl 7 or (z shl 3) or (x shr 1)
        val old: Int = skyLight!![i] and 0xff
        if (x and 1 == 0) {
            skyLight!![i] = (old and 0xf0 or (level and 0x0f)).toByte()
        } else {
            skyLight!![i] = (level and 0x0f shl 4 or (old and 0x0f)).toByte()
        }
    }

    @Override
    override fun getBlockLight(x: Int, y: Int, z: Int): Int {
        if (blockLight == null && !hasBlockLight) return 0
        blockLight = lightArray
        val l: Int = blockLight!![y shl 7 or (z shl 3) or (x shr 1)] and 0xff
        return if (x and 1 == 0) {
            l and 0x0f
        } else l shr 4
    }

    @Override
    override fun setBlockLight(x: Int, y: Int, z: Int, level: Int) {
        if (blockLight == null) {
            if (hasBlockLight) {
                blockLight = lightArray
            } else if (level == 0) {
                return
            } else {
                blockLight = ByteArray(2048)
            }
        }
        val i = y shl 7 or (z shl 3) or (x shr 1)
        val old: Int = blockLight!![i] and 0xff
        if (x and 1 == 0) {
            blockLight!![i] = (old and 0xf0 or (level and 0x0f)).toByte()
        } else {
            blockLight!![i] = (level and 0x0f shl 4 or (old and 0x0f)).toByte()
        }
    }

    @get:Override
    override val skyLightArray: ByteArray?
        get() {
            if (skyLight != null) return skyLight
            return if (hasSkyLight) {
                if (compressedLight != null) {
                    inflate()
                    return skyLight
                }
                EmptyChunkSection.EMPTY_SKY_LIGHT_ARR
            } else {
                EmptyChunkSection.EMPTY_LIGHT_ARR
            }
        }

    private fun inflate() {
        try {
            if (compressedLight != null && compressedLight!!.size != 0) {
                val inflated: ByteArray = Zlib.inflate(compressedLight)
                blockLight = Arrays.copyOfRange(inflated, 0, 2048)
                if (inflated.size > 2048) {
                    skyLight = Arrays.copyOfRange(inflated, 2048, 4096)
                } else {
                    skyLight = ByteArray(2048)
                    if (hasSkyLight) {
                        Arrays.fill(skyLight, 0xFF.toByte())
                    }
                }
                compressedLight = null
            } else {
                blockLight = ByteArray(2048)
                skyLight = ByteArray(2048)
                if (hasSkyLight) {
                    Arrays.fill(skyLight, 0xFF.toByte())
                }
            }
        } catch (e: IOException) {
            log.error("Failed to decompress a chunk section", e)
        }
    }

    @get:Override
    override val lightArray: ByteArray?
        get() {
            if (blockLight != null) return blockLight
            return if (hasBlockLight) {
                inflate()
                blockLight
            } else {
                EmptyChunkSection.EMPTY_LIGHT_ARR
            }
        }

    @get:Override
    override val isEmpty: Boolean
        get() = false

    private fun toXZY(raw: CharArray): ByteArray {
        val buffer: ByteArray = ThreadCache.byteCache6144.get()
        for (i in 0..4095) {
            buffer[i] = (raw[i] shr 4) as Byte
        }
        var i = 0
        var j = 4096
        while (i < 4096) {
            buffer[j] = (raw[i + 1] and 0xF shl 4 or (raw[i] and 0xF)) as Byte
            i += 2
            j++
        }
        return buffer
    }

    @Override
    @Synchronized
    override fun writeTo(@Nonnull stream: BinaryStream) {
        layerStorage!!.writeTo(stream)
    }

    @SuppressWarnings("java:S1905")
    @Nullable
    private fun saveData(
            storage: BlockStorage, idsBase: ByteArray, @Nullable idsExtra: ByteArray?,
            dataBase: NibbleArray, @Nullable dataExtra: NibbleArray?): List<ByteArray>? {
        val huge: Boolean = storage.hasBlockDataHuge()
        val big = huge || storage.hasBlockDataBig()
        val hugeList: List<ByteArray>? = if (big) ArrayList(if (huge) 3 else 1) else null
        if (big) {
            hugeList.add(ByteArray(BlockStorage.SECTION_SIZE))
        }
        storage.iterateStates label@{ bx, by, bz, state ->
            val anvil = getAnvilIndex(bx, by, bz)
            val blockId: Int = state.getBlockId()
            if (blockId == 0) {
                return@label
            }
            idsBase[anvil] = (blockId and 0xFF).toByte()
            if (idsExtra != null) {
                idsExtra[anvil] = (blockId ushr 8 and 0xFF).toByte()
            }
            @SuppressWarnings("deprecation") val unsignedIntData: Int = state.getBigDamage()
            dataBase.set(anvil, (unsignedIntData and 0x0F).toByte())
            if (dataExtra != null) {
                dataExtra.set(anvil, (unsignedIntData ushr 4 and 0x0F).toByte())
            }
            if (!big) {
                return@label
            }
            hugeList!![0][anvil] = (unsignedIntData ushr 8 and 0xFF).toByte()
            if (huge) {
                saveHugeData(hugeList, state, anvil, unsignedIntData)
            }
        }
        return hugeList
    }

    private fun saveHugeData(hugeList: List<ByteArray>?, state: BlockState, anvil: Int, intData: Int) {
        var intData = intData
        val bitSize: Int = state.getBitSize()
        if (bitSize <= 16) {
            return
        }
        intData = intData ushr 16
        var processedBits = 16
        var pos = 1
        while (processedBits < 32 && processedBits <= bitSize) {
            val blob = allocateBlob(hugeList, pos)
            blob[anvil] = (intData and 0xFF).toByte()
            processedBits += 8
            pos++
            intData = intData ushr 8
        }
        if (processedBits >= bitSize) {
            return
        }
        var hugeData: BigInteger = state.getHugeDamage().shiftRight(32)
        while (processedBits <= bitSize) {
            val blob = allocateBlob(hugeList, pos)
            blob[anvil] = hugeData.and(BYTE_MASK).byteValue()
            processedBits += 8
            pos++
            hugeData = hugeData.shiftRight(8)
        }
    }

    private fun allocateBlob(hugeList: List<ByteArray>?, pos: Int): ByteArray {
        val blob: ByteArray
        if (hugeList!!.size() <= pos) {
            blob = ByteArray(BlockStorage.SECTION_SIZE)
            hugeList.add(blob)
        } else {
            blob = hugeList[pos]
        }
        return blob
    }

    @Nonnull
    @Override
    @Synchronized
    override fun toNBT(): CompoundTag {
        val s = CompoundTag()
        compressStorageLayers()
        // For simplicity, not using the actual palette format to save in the disk
        // And for better compatibility, attempting to use the closest to the old format as possible
        // Version 0 = old format (single block storage, Blocks and Data tags only)
        // Version 1 = old format extended same as 0 but may have BlocksExtra and DataExtra
        // Version 7 = new format (multiple block storage, may have Blocks, BlocksExtra, Data and DataExtra)
        // Version 8 = not the same as network version 8 because it's not pallet, it's like 7 but everything is filled even when an entire section is empty
        s.putByte("Y", y)
        var version = SAVE_STORAGE_VERSION
        val storageList: ListTag<CompoundTag> = ListTag(STORAGE_TAG_NAME)
        val blockStorages: Int = Math.max(1, layerStorage!!.size())
        for (layer in 0 until blockStorages) {
            val storage: BlockStorage = layerStorage!!.getStorageOrEmpty(layer)
            var storageTag: CompoundTag
            if (layer == 0 && blockStorages == 1) {
                storageTag = s
                version = if (!storage.hasBlockDataExtras() && !storage.hasBlockIdExtras()) {
                    0
                } else {
                    1
                }
            } else {
                storageTag = CompoundTag()
            }
            if (version == 0 || storage.hasBlockIds()) {
                val idsBase = ByteArray(BlockStorage.SECTION_SIZE)
                val idsExtra = if (storage.hasBlockIdExtras()) ByteArray(BlockStorage.SECTION_SIZE) else null
                val dataBase = NibbleArray(BlockStorage.SECTION_SIZE)
                val dataExtra: NibbleArray? = if (storage.hasBlockDataExtras()) NibbleArray(BlockStorage.SECTION_SIZE) else null
                val dataHuge = saveData(storage, idsBase, idsExtra, dataBase, dataExtra)
                storageTag.putByteArray("Blocks", idsBase)
                storageTag.putByteArray("Data", dataBase.getData())
                if (idsExtra != null) {
                    storageTag.putByteArray("BlocksExtra", idsExtra)
                }
                if (dataExtra != null) {
                    storageTag.putByteArray("DataExtra", dataExtra.getData())
                }
                if (dataHuge != null) {
                    val hugeDataListTag: ListTag<ByteArrayTag> = ListTag(HUGE_TAG_NAME)
                    for (hugeData in dataHuge) {
                        hugeDataListTag.add(ByteArrayTag("", hugeData))
                    }
                    storageTag.putList(hugeDataListTag)
                }
            }
            if (version >= SAVE_STORAGE_VERSION) {
                storageList.add(storageTag)
            }
        }
        s.putByte("Version", version)
        s.putByte("ContentVersion", contentVersion)
        if (version >= SAVE_STORAGE_VERSION) {
            s.putList(storageList)
        }
        s.putByteArray("BlockLight", lightArray)
        s.putByteArray("SkyLight", skyLightArray)
        return s
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    @Synchronized
    override fun compressStorageLayers() {
        layerStorage!!.compress { storage: LayerStorage -> setLayerStorage(storage) }
    }

    fun compress(): Boolean {
        if (blockLight != null) {
            val arr1: ByteArray = blockLight
            hasBlockLight = !Utils.isByteArrayEmpty(arr1)
            val arr2: ByteArray
            if (skyLight != null) {
                arr2 = skyLight
                hasSkyLight = !Utils.isByteArrayEmpty(arr2)
            } else if (hasSkyLight) {
                arr2 = EmptyChunkSection.EMPTY_SKY_LIGHT_ARR
            } else {
                arr2 = EmptyChunkSection.EMPTY_LIGHT_ARR
                hasSkyLight = false
            }
            blockLight = null
            skyLight = null
            var toDeflate: ByteArray? = null
            if (hasBlockLight && hasSkyLight && arr2 != EmptyChunkSection.EMPTY_SKY_LIGHT_ARR) {
                toDeflate = Binary.appendBytes(arr1, arr2)
            } else if (hasBlockLight) {
                toDeflate = arr1
            }
            if (toDeflate != null) {
                try {
                    compressedLight = Zlib.deflate(toDeflate, 1)
                } catch (e: Exception) {
                    log.error("Error compressing the light data", e)
                }
            }
            return true
        }
        return false
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @Synchronized
    override fun scanBlocks(provider: LevelProvider, offsetX: Int, offsetZ: Int, min: BlockVector3, max: BlockVector3, condition: BiPredicate<BlockVector3?, BlockState?>): List<Block> {
        val storage: BlockStorage = layerStorage!!.getStorageOrNull(0) ?: return Collections.emptyList()
        val results: List<Block> = ArrayList()
        val current = BlockVector3()
        val offsetY = y shl 4
        val minX: Int = Math.max(0, min.x - offsetX)
        val minY: Int = Math.max(0, min.y - offsetY)
        val minZ: Int = Math.max(0, min.z - offsetZ)
        for (x in Math.min(max.x - offsetX, 15) downTo minX) {
            current.x = offsetX + x
            for (z in Math.min(max.z - offsetZ, 15) downTo minZ) {
                current.z = offsetZ + z
                for (y in Math.min(max.y - offsetY, 15) downTo minY) {
                    current.y = offsetY + y
                    val state: BlockState = storage.getBlockState(x, y, z)
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(provider.getLevel(), current, 0))
                    }
                }
            }
        }
        return results
    }

    @SneakyThrows(CloneNotSupportedException::class)
    @Nonnull
    override fun copy(): ChunkSection {
        return ChunkSection(
                y,
                layerStorage!!.clone(),
                if (blockLight == null) null else blockLight.clone(),
                if (skyLight == null) null else skyLight.clone(),
                if (compressedLight == null) null else compressedLight.clone(),
                hasBlockLight,
                hasSkyLight
        )
    }

    @get:Override
    override val maximumLayer: Int
        get() = 1

    @Override
    override fun hasBlocks(): Boolean {
        return layerStorage!!.hasBlocks()
    }

    companion object {
        const val STREAM_STORAGE_VERSION = 8
        const val SAVE_STORAGE_VERSION = 7
        private const val STORAGE_TAG_NAME = "Storage"
        private const val HUGE_TAG_NAME = "DataHyper"
        private val BYTE_MASK: BigInteger = BigInteger.valueOf(0xFF)
        private fun loadState(index: Int, blockId: Int, composedData: Int, hugeDataList: ListTag<ByteArrayTag>, hugeDataSize: Int): BlockState {
            return if (hugeDataSize == 0) {
                BlockState.of(blockId, composedData)
            } else if (hugeDataSize < 3) {
                loadHugeIntData(index, blockId, composedData, hugeDataList, hugeDataSize)
            } else if (hugeDataSize < 7) {
                loadHugeLongData(index, blockId, composedData, hugeDataList, hugeDataSize)
            } else {
                loadHugeBigData(index, blockId, composedData, hugeDataList, hugeDataSize)
            }
        }

        private fun loadHugeIntData(index: Int, blockId: Int, composedData: Int, hugeDataList: ListTag<ByteArrayTag>, hugeDataSize: Int): BlockState {
            var data = composedData
            for (dataIndex in 0 until hugeDataSize) {
                val longPart: Int = hugeDataList.get(dataIndex).data.get(index) and 0xFF shl 8 shl 8 * dataIndex
                data = data or longPart
            }
            return BlockState.of(blockId, data)
        }

        private fun loadHugeLongData(index: Int, blockId: Int, composedData: Int, hugeDataList: ListTag<ByteArrayTag>, hugeDataSize: Int): BlockState {
            var data = composedData.toLong()
            for (dataIndex in 0 until hugeDataSize) {
                val longPart: Long = hugeDataList.get(dataIndex).data.get(index) and 0xFFL shl 8 shl 8 * dataIndex
                data = data or longPart
            }
            return BlockState.of(blockId, data)
        }

        private fun loadHugeBigData(index: Int, blockId: Int, composedData: Int, hugeDataList: ListTag<ByteArrayTag>, hugeDataSize: Int): BlockState {
            var data: BigInteger = BigInteger.valueOf(composedData)
            for (dataIndex in 0 until hugeDataSize) {
                val hugePart: BigInteger = BigInteger.valueOf(hugeDataList.get(dataIndex).data.get(index) and 0xFFL shl 8).shiftLeft(8 * dataIndex)
                data = data.or(hugePart)
            }
            return BlockState.of(blockId, data)
        }

        private fun getStorageTagList(nbt: CompoundTag, version: Int): ListTag<CompoundTag> {
            val storageTagList: ListTag<CompoundTag>
            if (version == SAVE_STORAGE_VERSION || version == 8) {
                storageTagList = nbt.getList(STORAGE_TAG_NAME, CompoundTag::class.java)
            } else if (version == 0 || version == 1) {
                storageTagList = ListTag(STORAGE_TAG_NAME)
                storageTagList.add(nbt)
            } else {
                throw ChunkException("Unsupported chunk section version: $version")
            }
            return storageTagList
        }

        private fun composeBlockId(baseId: Byte, extraId: Byte): Int {
            return extraId and 0xFF shl 8 or (baseId and 0xFF)
        }

        private fun composeData(baseData: Byte, extraData: Byte): Int {
            return extraData and 0xF shl 4 or (baseData and 0xF)
        }

        private fun getAnvilIndex(x: Int, y: Int, z: Int): Int {
            return (y shl 8) + (z shl 4) + x // YZX
        }
    }
}