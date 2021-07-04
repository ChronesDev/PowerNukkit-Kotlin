package cn.nukkit.level.format.generic

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BaseFullChunk : FullChunk, ChunkManager {
    protected var entities: Map<Long, Entity>? = null
    protected var tiles: Map<Long, BlockEntity>? = null
    protected var tileList: Map<Integer, BlockEntity>? = null

    /**
     * encoded as:
     *
     * (x &lt;&lt; 4) | z
     */
    @get:Override
    var biomeIdArray: ByteArray?
        protected set
    protected var blocks: ByteArray?
    protected var data: ByteArray?

    @get:Override
    var blockSkyLightArray: ByteArray?
        protected set

    @get:Override
    var blockLightArray: ByteArray?
        protected set

    @get:Override
    var heightMapArray: ByteArray?
        protected set
    protected var NBTtiles: List<CompoundTag>? = null
    protected var NBTentities: List<CompoundTag>? = null
    protected var extraData: Map<Integer, Integer>? = null
    protected var provider: LevelProvider? = null
    protected var providerClass: Class<out LevelProvider?>? = null
    private var x = 0
    private var z = 0

    @get:Override
    var index: Long = 0
        private set
    var changes: Long = 0
        protected set
    protected var isInit = false
    protected var chunkPacket: BatchPacket? = null
    @Override
    fun clone(): BaseFullChunk? {
        val chunk: BaseFullChunk
        chunk = try {
            super.clone() as BaseFullChunk
        } catch (e: CloneNotSupportedException) {
            return null
        }
        if (biomeIdArray != null) {
            chunk.biomeIdArray = biomeIdArray.clone()
        }
        if (blocks != null) {
            chunk.blocks = blocks.clone()
        }
        if (data != null) {
            chunk.data = data.clone()
        }
        if (blockSkyLightArray != null) {
            chunk.blockSkyLightArray = blockSkyLightArray.clone()
        }
        if (blockLightArray != null) {
            chunk.blockLightArray = blockLightArray.clone()
        }
        if (heightMapArray != null) {
            chunk.heightMapArray = heightMapArray.clone()
        }
        return chunk
    }

    fun setChunkPacket(packet: BatchPacket?) {
        if (packet != null) {
            packet.trim()
        }
        chunkPacket = packet
    }

    fun getChunkPacket(): BatchPacket? {
        val pk: BatchPacket? = chunkPacket
        if (pk != null) {
            pk.trim()
        }
        return chunkPacket
    }

    @PowerNukkitOnly("Needed for level backward compatibility")
    @Since("1.3.0.0-PN")
    fun backwardCompatibilityUpdate(level: Level?) {
        // Does nothing here
    }

    fun initChunk() {
        if (getProvider() != null && !isInit) {
            var changed = false
            if (NBTentities != null) {
                getProvider().getLevel().timings.syncChunkLoadEntitiesTimer.startTiming()
                for (nbt in NBTentities!!) {
                    if (!nbt.contains("id")) {
                        this.setChanged()
                        continue
                    }
                    val pos: ListTag = nbt.getList("Pos")
                    if ((pos.get(0) as NumberTag).getData().intValue() shr 4 !== getX() || (pos.get(2) as NumberTag).getData().intValue() shr 4 !== getZ()) {
                        changed = true
                        continue
                    }
                    val entity: Entity = Entity.createEntity(nbt.getString("id"), this, nbt)
                    if (entity != null) {
                        changed = true
                    }
                }
                getProvider().getLevel().timings.syncChunkLoadEntitiesTimer.stopTiming()
                NBTentities = null
            }
            if (NBTtiles != null) {
                getProvider().getLevel().timings.syncChunkLoadBlockEntitiesTimer.startTiming()
                for (nbt in NBTtiles!!) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true
                            continue
                        }
                        if (nbt.getInt("x") shr 4 !== getX() || nbt.getInt("z") shr 4 !== getZ()) {
                            changed = true
                            continue
                        }
                        val blockEntity: BlockEntity = BlockEntity.createBlockEntity(nbt.getString("id"), this, nbt)
                        if (blockEntity == null) {
                            changed = true
                        }
                    }
                }
                getProvider().getLevel().timings.syncChunkLoadBlockEntitiesTimer.stopTiming()
                NBTtiles = null
            }
            if (changed) {
                this.setChanged()
            }
            isInit = true
        }
    }

    @Override
    fun getX(): Int {
        return x
    }

    @Override
    fun getZ(): Int {
        return z
    }

    @Override
    fun setPosition(x: Int, z: Int) {
        this.x = x
        this.z = z
        index = Level.chunkHash(x, z)
    }

    fun setX(x: Int) {
        this.x = x
        index = Level.chunkHash(x, getZ())
    }

    fun setZ(z: Int) {
        this.z = z
        index = Level.chunkHash(getX(), z)
    }

    @Override
    fun getProvider(): LevelProvider? {
        return provider
    }

    @Override
    fun setProvider(provider: LevelProvider?) {
        this.provider = provider
    }

    @Override
    fun getBiomeId(x: Int, z: Int): Int {
        return biomeIdArray!![x shl 4 or z] and 0xFF
    }

    @Override
    fun setBiomeId(x: Int, z: Int, biomeId: Byte) {
        biomeIdArray!![x shl 4 or z] = biomeId
        this.setChanged()
    }

    @Override
    fun getHeightMap(x: Int, z: Int): Int {
        return heightMapArray!![z shl 4 or x] and 0xFF
    }

    @Override
    fun setHeightMap(x: Int, z: Int, value: Int) {
        heightMapArray!![z shl 4 or x] = value.toByte()
    }

    @Override
    fun recalculateHeightMap() {
        for (z in 0..15) {
            for (x in 0..15) {
                recalculateHeightMapColumn(x, z)
            }
        }
    }

    @Override
    fun recalculateHeightMapColumn(x: Int, z: Int): Int {
        val max = getHighestBlockAt(x, z, false)
        var y: Int
        y = max
        while (y >= 0) {
            if (Block.lightFilter.get(getBlockIdAt(x, y, z)) > 1 || Block.diffusesSkyLight.get(getBlockIdAt(x, y, z))) {
                break
            }
            --y
        }
        setHeightMap(x, z, y + 1)
        return y + 1
    }

    @Override
    fun getBlockExtraData(x: Int, y: Int, z: Int): Int {
        val index: Int = Level.chunkBlockHash(x, y, z)
        return if (extraData != null && extraData!!.containsKey(index)) {
            extraData!![index]
        } else 0
    }

    @Override
    fun setBlockExtraData(x: Int, y: Int, z: Int, data: Int) {
        if (data == 0) {
            if (extraData != null) {
                extraData.remove(Level.chunkBlockHash(x, y, z))
            }
        } else {
            if (extraData == null) extraData = Int2ObjectOpenHashMap()
            extraData.put(Level.chunkBlockHash(x, y, z), data)
        }
        this.setChanged(true)
    }

    @Override
    fun populateSkyLight() {
        // basic light calculation
        for (z in 0..15) {
            for (x in 0..15) { // iterating over all columns in chunk
                val top = getHeightMap(x, z) - 1 // top-most block
                var y: Int
                y = 255
                while (y > top) {

                    // all the blocks above & including the top-most block in a column are exposed to sun and
                    // thus have a skylight value of 15
                    this.setBlockSkyLight(x, y, z, 15)
                    --y
                }
                var nextLight = 15 // light value that will be applied starting with the next block
                var nextDecrease = 0 // decrease that that will be applied starting with the next block

                // TODO: remove nextLight & nextDecrease, use only light & decrease variables
                y = top
                while (y >= 0) {
                    // going under the top-most block
                    nextLight -= nextDecrease
                    var light = nextLight // this light value will be applied for this block. The following checks are all about the next blocks
                    if (light < 0) {
                        light = 0
                    }
                    this.setBlockSkyLight(x, y, z, light)
                    if (light == 0) { // skipping block checks, because everything under a block that has a skylight value
                        // of 0 also has a skylight value of 0
                        --y
                        continue
                    }

                    // START of checks for the next block
                    val id: Int = this.getBlockId(x, y, z)
                    if (!Block.transparent.get(id)) { // if we encounter an opaque block, all the blocks under it will
                        // have a skylight value of 0 (the block itself has a value of 15, if it's a top-most block)
                        nextLight = 0
                    } else if (Block.diffusesSkyLight.get(id)) {
                        nextDecrease += 1 // skylight value decreases by one for each block under a block
                        // that diffuses skylight. The block itself has a value of 15 (if it's a top-most block)
                    } else {
                        nextDecrease -= Block.lightFilter.get(id) // blocks under a light filtering block will have a skylight value
                        // decreased by the lightFilter value of that block. The block itself
                        // has a value of 15 (if it's a top-most block)
                    }
                    --y
                }
            }
        }
    }

    @Override
    fun getHighestBlockAt(x: Int, z: Int): Int {
        return this.getHighestBlockAt(x, z, true)
    }

    @Override
    fun getHighestBlockAt(x: Int, z: Int, cache: Boolean): Int {
        if (cache) {
            val h = getHeightMap(x, z)
            if (h != 0 && h != 255) {
                return h
            }
        }
        for (y in 255 downTo 0) {
            if (getBlockId(x, y, z) !== 0x00) {
                setHeightMap(x, z, y)
                return y
            }
        }
        return 0
    }

    @Override
    fun addEntity(entity: Entity) {
        if (entities == null) {
            entities = Long2ObjectOpenHashMap()
        }
        entities.put(entity.getId(), entity)
        if (entity !is Player && isInit) {
            this.setChanged()
        }
    }

    @Override
    fun removeEntity(entity: Entity) {
        if (entities != null) {
            entities.remove(entity.getId())
            if (entity !is Player && isInit) {
                this.setChanged()
            }
        }
    }

    @Override
    fun addBlockEntity(blockEntity: BlockEntity) {
        if (tiles == null) {
            tiles = Long2ObjectOpenHashMap()
            tileList = Int2ObjectOpenHashMap()
        }
        tiles.put(blockEntity.getId(), blockEntity)
        val index: Int = blockEntity.getFloorZ() and 0x0f shl 12 or (blockEntity.getFloorX() and 0x0f shl 8) or (blockEntity.getFloorY() and 0xff)
        if (tileList!!.containsKey(index) && !tileList!![index].equals(blockEntity)) {
            val entity: BlockEntity? = tileList!![index]
            tiles.remove(entity.getId())
            entity.close()
        }
        tileList.put(index, blockEntity)
        if (isInit) {
            this.setChanged()
        }
    }

    @Override
    fun removeBlockEntity(blockEntity: BlockEntity) {
        if (tiles != null) {
            tiles.remove(blockEntity.getId())
            val index: Int = blockEntity.getFloorZ() and 0x0f shl 12 or (blockEntity.getFloorX() and 0x0f shl 8) or (blockEntity.getFloorY() and 0xff)
            tileList.remove(index)
            if (isInit) {
                this.setChanged()
            }
        }
    }

    @Override
    fun getEntities(): Map<Long, Entity> {
        return if (entities == null) Collections.emptyMap() else entities!!
    }

    @get:Override
    val blockEntities: Map<Long, Any>
        get() = if (tiles == null) Collections.emptyMap() else tiles!!

    @get:Override
    val blockExtraDataArray: Map<Any, Any>
        get() = if (extraData == null) Collections.emptyMap() else extraData!!

    @Override
    fun getTile(x: Int, y: Int, z: Int): BlockEntity? {
        return if (tileList != null) tileList!![z shl 12 or (x shl 8) or y] else null
    }

    @get:Override
    val isLoaded: Boolean
        get() = getProvider() != null && getProvider().isChunkLoaded(getX(), getZ())

    @Override
    @Throws(IOException::class)
    fun load(): Boolean {
        return this.load(true)
    }

    @Override
    @Throws(IOException::class)
    fun load(generate: Boolean): Boolean {
        return getProvider() != null && getProvider().getChunk(getX(), getZ(), true) != null
    }

    @Override
    @Throws(Exception::class)
    fun unload(): Boolean {
        return this.unload(true, true)
    }

    @Override
    @Throws(Exception::class)
    fun unload(save: Boolean): Boolean {
        return this.unload(save, true)
    }

    @Override
    fun unload(save: Boolean, safe: Boolean): Boolean {
        val provider: LevelProvider = getProvider() ?: return true
        if (save && changes != 0L) {
            provider.saveChunk(getX(), getZ())
        }
        if (safe) {
            for (entity in getEntities().values()) {
                if (entity is Player) {
                    return false
                }
            }
        }
        for (entity in ArrayList(getEntities().values())) {
            if (entity is Player) {
                continue
            }
            entity.close()
        }
        for (blockEntity in ArrayList(blockEntities.values())) {
            blockEntity.close()
        }
        this.provider = null
        return true
    }

    @Override
    fun hasChanged(): Boolean {
        return changes != 0L
    }

    @Override
    fun setChanged() {
        changes++
        chunkPacket = null
    }

    @Override
    fun setChanged(changed: Boolean) {
        if (changed) {
            setChanged()
        } else {
            changes = 0
        }
    }

    @Override
    fun toFastBinary(): ByteArray {
        return this.toBinary()
    }

    @get:Override
    @set:Override
    var isLightPopulated: Boolean
        get() = true
        set(value) {}

    @Override
    fun setLightPopulated() {
        isLightPopulated = true
    }

    @Override
    fun getBlockIdAt(x: Int, y: Int, z: Int): Int {
        return getBlockIdAt(x, y, z, 0)
    }

    @Override
    fun getBlockIdAt(x: Int, y: Int, z: Int, layer: Int): Int {
        return if (x shr 4 == getX() && z shr 4 == getZ()) {
            getBlockId(x and 15, y, z and 15, layer)
        } else 0
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, fullId: Int) {
        setFullBlockId(x, y, z, 0, fullId)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockFullIdAt(x: Int, y: Int, z: Int, layer: Int, fullId: Int) {
        if (x shr 4 == getX() && z shr 4 == getZ()) {
            setFullBlockId(x and 15, y, z and 15, layer, fullId)
        }
    }

    @Override
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId))
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean {
        return setBlockStateAtLayer(x, y, z, layer, BlockState.of(blockId, meta))
    }

    @Override
    fun setBlockIdAt(x: Int, y: Int, z: Int, id: Int) {
        setBlockIdAt(x, y, z, 0, id)
    }

    @Override
    fun setBlockIdAt(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        if (x shr 4 == getX() && z shr 4 == getZ()) {
            setBlockId(x and 15, y, z and 15, layer, id)
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockAt(x: Int, y: Int, z: Int, id: Int, data: Int) {
        if (x shr 4 == getX() && z shr 4 == getZ()) {
            setBlockState(x and 15, y, z and 15, BlockState.of(id, data))
        }
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun getBlockDataAt(x: Int, y: Int, z: Int): Int {
        return getBlockDataAt(x, y, z, 0)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    @PowerNukkitDifference(info = "Was returning the block id instead of the data", since = "1.4.0.0-PN")
    fun getBlockDataAt(x: Int, y: Int, z: Int, layer: Int): Int {
        return if (x shr 4 == getX() && z shr 4 == getZ()) {
            getBlockData(x and 15, y, z and 15, layer)
        } else 0
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockDataAt(x: Int, y: Int, z: Int, data: Int) {
        setBlockDataAt(x, y, z, 0, data)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Override
    fun setBlockDataAt(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        if (x shr 4 == getX() && z shr 4 == getZ()) {
            setBlockData(x and 15, y, z and 15, layer, data)
        }
    }

    @Override
    fun getChunk(chunkX: Int, chunkZ: Int): BaseFullChunk? {
        return if (chunkX == getX() && chunkZ == getZ()) this else null
    }

    @Override
    fun setChunk(chunkX: Int, chunkZ: Int) {
        setChunk(chunkX, chunkZ, null)
    }

    @Override
    fun setChunk(chunkX: Int, chunkZ: Int, chunk: BaseFullChunk?) {
        throw UnsupportedOperationException()
    }

    @get:Override
    val seed: Long
        get() {
            throw UnsupportedOperationException("Chunk does not have a seed")
        }

    fun compress(): Boolean {
        val pk: BatchPacket? = chunkPacket
        if (pk != null) {
            pk.trim()
            return true
        }
        return false
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun scanBlocks(min: BlockVector3, max: BlockVector3, condition: BiPredicate<BlockVector3?, BlockState?>): Stream<Block> {
        val offsetX = getX() shl 4
        val offsetZ = getZ() shl 4
        val results: List<Block> = ArrayList()
        val current = BlockVector3()
        val minX: Int = Math.max(0, min.x - offsetX)
        val minY: Int = Math.max(0, min.y)
        val minZ: Int = Math.max(0, min.z - offsetZ)
        for (x in Math.min(max.x - offsetX, 15) downTo minX) {
            current.x = offsetX + x
            for (z in Math.min(max.z - offsetZ, 15) downTo minZ) {
                current.z = offsetZ + z
                for (y in Math.min(max.y, 255) downTo minY) {
                    current.y = y
                    val state: BlockState = getBlockState(x, y, z)
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(getProvider().getLevel(), current, 0))
                    }
                }
            }
        }
        return results.stream()
    }
}