package cn.nukkit.level.format.generic

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
class EmptyChunkSection(@get:Override val y: Int) : ChunkSection {
    companion object {
        @SuppressWarnings("java:S2386")
        val EMPTY = arrayOfNulls<EmptyChunkSection>(16)
        private const val MODIFICATION_ERROR_MESSAGE = "Tried to modify an empty Chunk"
        private val EMPTY_2KB = ByteArray(2048)

        @get:Override
        val lightArray = EMPTY_2KB
            get() = Companion.field

        @get:Override
        val skyLightArray = ByteArray(2048)
            get() = Companion.field

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val EMPTY_ID_ARRAY = ByteArray(4096)

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val EMPTY_DATA_ARRAY = EMPTY_2KB
        private val EMPTY_CHUNK_DATA: ByteArray

        init {
            for (y in EMPTY.indices) {
                EMPTY[y] = EmptyChunkSection(y)
            }
        }

        init {
            Arrays.fill(skyLightArray, 255.toByte())
        }

        init {
            val stream = BinaryStream()
            cn.nukkit.level.format.generic.stream.putByte(cn.nukkit.level.format.anvil.ChunkSection.STREAM_STORAGE_VERSION as Byte)
            cn.nukkit.level.format.generic.stream.putByte(0.toByte())
            EMPTY_CHUNK_DATA = cn.nukkit.level.format.generic.stream.getBuffer()
        }
    }

    @Override
    fun getBlockId(x: Int, y: Int, z: Int): Int {
        return 0
    }

    @Override
    fun getBlockId(x: Int, y: Int, z: Int, layer: Int): Int {
        return 0
    }

    @Override
    fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return 0
    }

    @Nonnull
    @Override
    fun getBlockState(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return BlockState.AIR
    }

    @Override
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean {
        if (blockId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Nonnull
    @Override
    fun getAndSetBlock(x: Int, y: Int, z: Int, layer: Int, block: Block): Block {
        if (block.getId() !== 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return Block.get(0)
    }

    @Nonnull
    @Override
    fun getAndSetBlock(x: Int, y: Int, z: Int, block: Block): Block {
        if (block.getId() !== 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return Block.get(0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    fun getAndSetBlockState(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): BlockState {
        if (!BlockState.AIR.equals(state)) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return BlockState.AIR
    }

    @Override
    fun setBlockId(x: Int, y: Int, z: Int, layer: Int, id: Int) {
        if (id != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @Override
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int): Boolean {
        if (blockId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int, meta: Int): Boolean {
        if (blockId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean {
        if (blockId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Override
    fun setBlockStateAtLayer(x: Int, y: Int, z: Int, layer: Int, state: BlockState): Boolean {
        if (!state.equals(BlockState.AIR)) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Override
    fun setBlockId(x: Int, y: Int, z: Int, id: Int) {
        if (id != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun getBlockData(x: Int, y: Int, z: Int): Int {
        return 0
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun getBlockData(x: Int, y: Int, z: Int, layer: Int): Int {
        return 0
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setBlockData(x: Int, y: Int, z: Int, data: Int) {
        if (data != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setBlockData(x: Int, y: Int, z: Int, layer: Int, data: Int) {
        if (data != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setFullBlockId(x: Int, y: Int, z: Int, fullId: Int): Boolean {
        if (fullId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun setFullBlockId(x: Int, y: Int, z: Int, layer: Int, fullId: Int): Boolean {
        if (fullId != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
        return false
    }

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @Override
    fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int {
        return 0
    }

    @Override
    fun getBlockLight(x: Int, y: Int, z: Int): Int {
        return 0
    }

    @Override
    fun setBlockLight(x: Int, y: Int, z: Int, level: Int) {
        if (level != 0) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @Override
    fun getBlockSkyLight(x: Int, y: Int, z: Int): Int {
        return 15
    }

    @Override
    fun setBlockSkyLight(x: Int, y: Int, z: Int, level: Int) {
        if (level != 15) throw ChunkException(MODIFICATION_ERROR_MESSAGE)
    }

    @get:Override
    val isEmpty: Boolean
        get() = true

    @Override
    fun writeTo(@Nonnull stream: BinaryStream) {
        stream.put(EMPTY_CHUNK_DATA)
    }

    @get:Override
    val maximumLayer: Int
        get() = 0

    @Nonnull
    @Override
    fun toNBT(): CompoundTag {
        return CompoundTag()
    }

    @Nonnull
    @Override
    fun copy(): EmptyChunkSection {
        return this
    }

    @get:Override
    @get:Since("1.3.1.0-PN")
    @get:PowerNukkitOnly
    @set:Override
    @set:Since("1.3.1.0-PN")
    @set:PowerNukkitOnly
    var contentVersion: Int
        get() = ChunkUpdater.getCurrentContentVersion()
        set(contentVersion) {
            if (contentVersion != contentVersion) {
                throw ChunkException(MODIFICATION_ERROR_MESSAGE)
            }
        }

    @Override
    fun getBlockChangeStateAbove(x: Int, y: Int, z: Int): Int {
        return 0
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun scanBlocks(provider: LevelProvider?, offsetX: Int, offsetZ: Int, min: BlockVector3?, max: BlockVector3?, condition: BiPredicate<BlockVector3?, BlockState?>?): List<Block> {
        return Collections.emptyList()
    }
}