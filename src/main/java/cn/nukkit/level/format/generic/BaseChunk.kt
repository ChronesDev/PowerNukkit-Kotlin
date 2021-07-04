package cn.nukkit.level.format.generic

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
abstract class BaseChunk : BaseFullChunk(), Chunk {
    private var delayPaletteUpdates = false
    protected var sections: Array<ChunkSection?>?

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    @Override
    override fun backwardCompatibilityUpdate(level: Level?) {
        ChunkUpdater.backwardCompatibilityUpdate(level, this)
    }

    @Override
    override fun clone(): BaseChunk? {
        val chunk = super.clone() as BaseChunk?
        if (this.biomes != null) chunk.biomes = this.biomes.clone()
        chunk.heightMap = heightMapArray.clone()
        if (sections != null && sections!![0] != null) {
            chunk!!.sections = arrayOfNulls<ChunkSection>(sections!!.size)
            for (i in sections.indices) {
                chunk.sections!![i] = sections!![i].copy()
            }
        }
        return chunk
    }

    private fun removeInvalidTile(x: Int, y: Int, z: Int) {
        val entity: BlockEntity = getTile(x, y, z)
        if (entity != null) {
            try {
                if (!entity.closed && entity.isBlockEntityValid()) {
                    return
                }
            } catch (e: Exception) {
                try {
                    log.warn("Block entity validation of {} at {}, {} {} {} failed, removing as invalid.",
                            entity.getClass().getName(),
                            getProvider().getLevel().getName(),
                            entity.x,
                            entity.y,
                            entity.z,
                            e
                    )
                } catch (e2: Exception) {
                    e.addSuppressed(e2)
                    log.warn("Block entity validation failed", e)
                }
            }
            removeBlockEntity(entity)
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    override fun scanBlocks(min: BlockVector3, max: BlockVector3, condition: BiPredicate<BlockVector3?, BlockState?>?): Stream<Block> {
        val offsetX: Int = getX() shl 4
        val offsetZ: Int = getZ() shl 4
        return IntStream.rangeClosed(min.getChunkSectionY(), max.getChunkSectionY())
                .filter { sectionY -> sectionY >= 0 && sectionY < sections!!.size }
                .mapToObj { sectionY -> sections!![sectionY] }
                .filter { section -> !section.isEmpty() }.parallel()
                .map { section -> section.scanBlocks(getProvider(), offsetX, offsetZ, min, max, condition) }
                .flatMap(Collection::stream)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return getFullBlock(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int {
        return sections!![y shr 4].getFullBlock(x, y and 0x0f, z, layer)
    }

    @PowerNukkitOnly
    @Override
    fun getBlockState(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return sections!![y shr 4].getBlockState(x, y and 0x0f, z, layer)
    }

    @PowerNukkitOnly
    @Override
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId))
    }

    @Override
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int): Boolean {
        return setBlockState(x, y, z, BlockState.of(blockId))
    }

    @Override
    fun getAndSetBlock(x: Int, y: Int, z: Int, block: Block): Block {
        return getAndSetBlock(x, y, z, 0, block)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun getAndSetBlockState(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): BlockState {
        val sectionY = y shr 4
        return try {
            setChanged()
            getOrCreateMutableSection(sectionY).getAndSetBlockState(x, y and 0x0f, z, layer, state)
        } finally {
            removeInvalidTile(x, y, z)
        }
    }

    @PowerNukkitOnly
    @Override
    fun getAndSetBlock(x: Int, y: Int, z: Int, layer: Int, block: Block): Block {
        val state: BlockState = getAndSetBlockState(x, y, z, layer, block.getCurrentState())
        return try {
            state.getBlock()
        } catch (e: InvalidBlockStateException) {
            BlockUnknown(state.getBlockId(), state.getExactIntStorage())
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    fun setFullBlockId(x: Int, y: Int, z: Int, fullId: Int): Boolean {
        return this.setFullBlockId(x, y, z, 0, fullId)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    @Override
    @PowerNukkitOnly
    fun setFullBlockId(x: Int, y: Int, z: Int, layer: Int, fullId: Int): Boolean {
        val sectionY = y shr 4
        return try {
            setChanged()
            getOrCreateMutableSection(sectionY).setFullBlockId(x, y and 0x0f, z, layer, fullId)
        } finally {
            removeInvalidTile(x, y, z)
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int, meta: Int): Boolean {
        return this.setBlockAtLayer(x, y, z, 0, blockId, meta)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setBlockStateAtLayer(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean {
        val sectionY = y shr 4
        return try {
            setChanged()
            getOrCreateMutableSection(sectionY).setBlockStateAtLayer(x, y and 0x0f, z, layer, state)
        } finally {
            removeInvalidTile(x, y, z)
        }
    }

    private fun getOrCreateMutableSection(sectionY: Int): ChunkSection? {
        val section: ChunkSection? = sections!![sectionY]
        if (section.isEmpty()) {
            createChunkSection(sectionY)
            return sections!![sectionY]
        }
        return section
    }

    private fun createChunkSection(sectionY: Int) {
        try {
            setInternalSection(sectionY.toFloat(), this.providerClass.getMethod("createChunkSection", Int::class.javaPrimitiveType).invoke(this.providerClass, sectionY) as ChunkSection)
        } catch (e: IllegalAccessException) {
            log.error("Failed to create ChunkSection", e)
            throw ChunkException(e)
        } catch (e: InvocationTargetException) {
            log.error("Failed to create ChunkSection", e)
            throw ChunkException(e)
        } catch (e: NoSuchMethodException) {
            log.error("Failed to create ChunkSection", e)
            throw ChunkException(e)
        }
    }

    @Override
    fun setBlockId(x: Int, y: Int, z: Int, id: Int) {
        setBlockStateAtLayer(x, y, z, 0, BlockState.of(id))
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @PowerNukkitOnly
    fun setBlockId(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        val sectionY = y shr 4
        try {
            setChanged()
            getOrCreateMutableSection(sectionY).setBlockId(x, y and 0x0f, z, layer, id)
        } finally {
            removeInvalidTile(x, y, z)
        }
    }

    @Override
    fun getBlockId(x: Int, y: Int, z: Int): Int {
        return getBlockId(x, y, z, 0)
    }

    @Override
    @PowerNukkitOnly
    fun getBlockId(x: Int, y: Int, z: Int, layer: Int): Int {
        return sections!![y shr 4].getBlockId(x, y and 0x0f, z, layer)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getBlockData(x: Int, y: Int, z: Int): Int {
        return getBlockData(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getBlockData(x: Int, y: Int, z: Int, layer: Int): Int {
        return sections!![y shr 4].getBlockData(x, y and 0x0f, z, layer)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockData(x: Int, y: Int, z: Int, data: Int) {
        setBlockData(x, y, z, 0, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockData(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        val sectionY = y shr 4
        try {
            setChanged()
            getOrCreateMutableSection(sectionY).setBlockData(x, y and 0x0f, z, layer, data)
        } finally {
            removeInvalidTile(x, y, z)
        }
    }

    @Override
    fun getBlockSkyLight(x: Int, y: Int, z: Int): Int {
        return sections!![y shr 4].getBlockSkyLight(x, y and 0x0f, z)
    }

    @Override
    fun setBlockSkyLight(x: Int, y: Int, z: Int, level: Int) {
        val sectionY = y shr 4
        getOrCreateMutableSection(sectionY).setBlockSkyLight(x, y and 0x0f, z, level)
        setChanged()
    }

    @Override
    fun getBlockLight(x: Int, y: Int, z: Int): Int {
        return sections!![y shr 4].getBlockLight(x, y and 0x0f, z)
    }

    @Override
    fun setBlockLight(x: Int, y: Int, z: Int, level: Int) {
        val sectionY = y shr 4
        getOrCreateMutableSection(sectionY).setBlockLight(x, y and 0x0f, z, level)
        setChanged()
    }

    @Override
    fun isSectionEmpty(fY: Float): Boolean {
        return sections!![fY.toInt()].isEmpty()
    }

    @Override
    fun getSection(fY: Float): ChunkSection? {
        return sections!![fY.toInt()]
    }

    @Override
    fun setSection(fY: Float, section: ChunkSection): Boolean {
        if (!section.hasBlocks()) {
            sections!![fY.toInt()] = EmptyChunkSection.EMPTY.get(fY.toInt())
        } else {
            sections!![fY.toInt()] = section
        }
        setChanged()
        return true
    }

    private fun setInternalSection(fY: Float, section: ChunkSection) {
        if (isPaletteUpdatesDelayed) {
            section.delayPaletteUpdates()
        }
        sections!![fY.toInt()] = section
        setChanged()
    }

    @Override
    @Throws(IOException::class)
    override fun load(): Boolean {
        return this.load(true)
    }

    @Override
    @Throws(IOException::class)
    override fun load(generate: Boolean): Boolean {
        return getProvider() != null && getProvider().getChunk(this.getX(), this.getZ(), true) != null
    }

    @get:Override
    override var blockSkyLightArray: ByteArray?
        get() {
            val buffer: ByteBuffer = ByteBuffer.allocate(2048 * SECTION_COUNT)
            for (y in 0 until SECTION_COUNT) {
                buffer.put(sections!![y].getSkyLightArray())
            }
            return buffer.array()
        }
        set(blockSkyLightArray) {
            super.blockSkyLightArray = blockSkyLightArray
        }

    @get:Override
    override var blockLightArray: ByteArray?
        get() {
            val buffer: ByteBuffer = ByteBuffer.allocate(2048 * SECTION_COUNT)
            for (y in 0 until SECTION_COUNT) {
                buffer.put(sections!![y].getLightArray())
            }
            return buffer.array()
        }
        set(blockLightArray) {
            super.blockLightArray = blockLightArray
        }

    @Override
    fun getSections(): Array<ChunkSection?>? {
        return sections
    }

    @get:Override
    override var heightMapArray: ByteArray?
        get() = this.heightMap
        set(heightMapArray) {
            super.heightMapArray = heightMapArray
        }

    @Override
    override fun getProvider(): LevelProvider? {
        return this.provider
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    fun setBlockStateAt(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, state)
    }

    @Override
    fun getBlockStateAt(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return getBlockState(x, y, z, layer)
    }

    @Override
    fun isBlockChangeAllowed(x: Int, y: Int, z: Int): Boolean {
        var y = y
        for (section in sections!!) {
            if (section.getBlockChangeStateAbove(x, 0, z) === 3) { // Border
                return false
            }
        }
        if (y <= 0) {
            return sections!![0].getBlockChangeStateAbove(x, 0, z) === 0
        }
        var sectionY = y shr 4
        y = y and 0xF
        while (sectionY >= 0) {
            y = when (sections!![sectionY].getBlockChangeStateAbove(x, y, z)) {
                1, 3 -> return false
                2 -> return if (sectionY == y shr 4) {
                    sections!![sectionY].getBlockId(x, y, z, 0) !== BlockID.ALLOW
                } else {
                    true
                }
                else -> 0xF
            }
            sectionY--
        }
        return true
    }

    @Nonnull
    @Override
    fun findBorders(x: Int, z: Int): List<Block> {
        var borders: List<Block>? = null
        for (section in sections!!) {
            if (section.getBlockChangeStateAbove(x, 0, z) === 3) {
                for (y in 0..0xe) {
                    val blockState: BlockState = section.getBlockState(x, y, z, 0)
                    if (blockState.getBlockId() === BlockID.BORDER_BLOCK) {
                        if (borders == null) {
                            borders = ArrayList(3)
                        }
                        borders.add(blockState.getBlock(provider.getLevel(), x, y, z, 0))
                    }
                }
            }
        }
        return borders ?: Collections.emptyList()
    }

    @Override
    fun isBlockedByBorder(x: Int, z: Int): Boolean {
        for (section in sections!!) {
            if (section.getBlockChangeStateAbove(x, 0, z) === 3) {
                return true
            }
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun delayPaletteUpdates() {
        val sections: Array<ChunkSection?>? = sections
        if (sections != null) {
            for (section in sections) {
                if (section != null) {
                    section.delayPaletteUpdates()
                }
            }
        }
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isPaletteUpdatesDelayed: Boolean
        get() = delayPaletteUpdates
        set(delayPaletteUpdates) {
            this.delayPaletteUpdates = delayPaletteUpdates
            if (delayPaletteUpdates) {
                delayPaletteUpdates()
            }
        }
}