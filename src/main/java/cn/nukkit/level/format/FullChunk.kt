package cn.nukkit.level.format

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface FullChunk : Cloneable {
    var x: Int
    var z: Int
    fun setPosition(x: Int, z: Int) {
        x = x
        z = z
    }

    val index: Long
    var provider: cn.nukkit.level.format.LevelProvider?

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getBlockRuntimeId(x: Int, y: Int, z: Int): Int {
        return getBlockRuntimeId(x, y, z, 0)
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getBlockRuntimeId(x: Int, y: Int, z: Int, layer: Int): Int {
        return getBlockState(x, y, z, layer).getRuntimeId()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockState(x: Int, y: Int, z: Int): BlockState? {
        return getBlockState(x, y, z, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockState(x: Int, y: Int, z: Int, layer: Int): BlockState {
        val full = getFullBlock(x, y, z, layer)
        return BlockState.of(full shr Block.DATA_BITS, full and Block.DATA_MASK)
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown", replaceWith = "getAndSetBlockState")
    fun getAndSetBlock(x: Int, y: Int, z: Int, block: Block?): Block?

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown", replaceWith = "getAndSetBlockState")
    @PowerNukkitOnly
    fun getAndSetBlock(x: Int, y: Int, z: Int, layer: Int, block: Block?): Block?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAndSetBlockState(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): BlockState?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAndSetBlockState(x: Int, y: Int, z: Int, state: BlockState?): BlockState? {
        return getAndSetBlockState(x, y, z, 0, state)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlock(int x, int y, int z, int  blockId, int  meta)")
    fun setFullBlockId(x: Int, y: Int, z: Int, fullId: Int): Boolean {
        return setFullBlockId(x, y, z, 0, fullId shr Block.DATA_BITS)
    }

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockAtLayer(int x, int y, int z, int layer, int  blockId)")
    fun setFullBlockId(x: Int, y: Int, z: Int, layer: Int, fullId: Int): Boolean {
        return setBlockAtLayer(x, y, z, layer, fullId shr Block.DATA_BITS, fullId and Block.DATA_MASK)
    }

    fun setBlock(x: Int, y: Int, z: Int, blockId: Int): Boolean

    @PowerNukkitOnly
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockState(x: Int, y: Int, z: Int, state: BlockState?): Boolean {
        return setBlockStateAtLayer(x, y, z, 0, state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockStateAtLayer(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int, meta: Int): Boolean

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean
    fun getBlockId(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    fun getBlockId(x: Int, y: Int, z: Int, layer: Int): Int
    fun setBlockId(x: Int, y: Int, z: Int, id: Int)

    @PowerNukkitOnly
    fun setBlockId(x: Int, y: Int, z: Int, layer: Int, id: Int)
    fun getBlockData(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    fun getBlockData(x: Int, y: Int, z: Int, layer: Int): Int
    fun setBlockData(x: Int, y: Int, z: Int, data: Int)

    @PowerNukkitOnly
    fun setBlockData(x: Int, y: Int, z: Int, layer: Int, data: Int)
    fun getBlockExtraData(x: Int, y: Int, z: Int): Int
    fun setBlockExtraData(x: Int, y: Int, z: Int, data: Int)
    fun getBlockSkyLight(x: Int, y: Int, z: Int): Int
    fun setBlockSkyLight(x: Int, y: Int, z: Int, level: Int)
    fun getBlockLight(x: Int, y: Int, z: Int): Int
    fun setBlockLight(x: Int, y: Int, z: Int, level: Int)
    fun getHighestBlockAt(x: Int, z: Int): Int
    fun getHighestBlockAt(x: Int, z: Int, cache: Boolean): Int
    fun getHeightMap(x: Int, z: Int): Int
    fun setHeightMap(x: Int, z: Int, value: Int)
    fun recalculateHeightMap()
    fun recalculateHeightMapColumn(chunkX: Int, chunkZ: Int): Int
    fun populateSkyLight()
    fun getBiomeId(x: Int, z: Int): Int
    fun setBiomeId(x: Int, z: Int, biomeId: Byte)
    fun setBiomeId(x: Int, z: Int, biomeId: Int) {
        setBiomeId(x, z, biomeId.toByte())
    }

    fun setBiome(x: Int, z: Int, biome: Biome) {
        setBiomeId(x, z, biome.getId() as Byte)
    }

    var isLightPopulated: Boolean
    fun setLightPopulated()
    var isPopulated: Boolean
    fun setPopulated()
    var isGenerated: Boolean
    fun setGenerated()
    fun addEntity(entity: Entity?)
    fun removeEntity(entity: Entity?)
    fun addBlockEntity(blockEntity: BlockEntity?)
    fun removeBlockEntity(blockEntity: BlockEntity?)
    val entities: Map<Long?, Any?>?
    val blockEntities: Map<Long?, Any?>?
    fun getTile(x: Int, y: Int, z: Int): BlockEntity?
    val isLoaded: Boolean

    @Throws(IOException::class)
    fun load(): Boolean

    @Throws(IOException::class)
    fun load(generate: Boolean): Boolean

    @Throws(Exception::class)
    fun unload(): Boolean

    @Throws(Exception::class)
    fun unload(save: Boolean): Boolean

    @Throws(Exception::class)
    fun unload(save: Boolean, safe: Boolean): Boolean
    fun initChunk()
    val biomeIdArray: ByteArray?
    val heightMapArray: ByteArray?
    val blockExtraDataArray: Map<Any?, Any?>?
    val blockSkyLightArray: ByteArray?
    val blockLightArray: ByteArray?
    fun toBinary(): ByteArray?
    fun toFastBinary(): ByteArray?
    fun hasChanged(): Boolean
    fun setChanged()
    fun setChanged(changed: Boolean)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isBlockChangeAllowed(x: Int, y: Int, z: Int): Boolean

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun findBorders(x: Int, z: Int): List<Block?>?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isBlockedByBorder(x: Int, z: Int): Boolean
}