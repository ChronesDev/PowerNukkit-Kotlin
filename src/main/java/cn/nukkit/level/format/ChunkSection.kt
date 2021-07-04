package cn.nukkit.level.format

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ParametersAreNonnullByDefault
interface ChunkSection {
    val y: Int
    fun getBlockId(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    fun getBlockId(x: Int, y: Int, z: Int, layer: Int): Int
    fun setBlockId(x: Int, y: Int, z: Int, id: Int)

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    fun getBlockData(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    fun getBlockData(x: Int, y: Int, z: Int, layer: Int): Int

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    fun setBlockData(x: Int, y: Int, z: Int, data: Int)

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun setBlockData(x: Int, y: Int, z: Int, layer: Int, data: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int, layer: Int): Int

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockState(x: Int, y: Int, z: Int): BlockState {
        return getBlockState(x, y, z, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockState(x: Int, y: Int, z: Int, layer: Int): BlockState {
        return BlockState.of(getBlockId(x, y, z, layer), getBlockData(x, y, z, layer))
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown", replaceWith = "getAndSetBlockState")
    @PowerNukkitOnly
    @Nonnull
    fun getAndSetBlock(x: Int, y: Int, z: Int, layer: Int, block: Block?): Block?

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "If the stored state is invalid, returns a BlockUnknown", replaceWith = "getAndSetBlockState")
    @Nonnull
    fun getAndSetBlock(x: Int, y: Int, z: Int, block: Block?): Block?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAndSetBlockState(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): BlockState?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAndSetBlockState(x: Int, y: Int, z: Int, state: BlockState?): BlockState? {
        return getAndSetBlockState(x, y, z, 0, state)
    }

    @PowerNukkitOnly
    fun setBlockId(x: Int, y: Int, z: Int, layer: Int, id: Int)

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockState(int x, int y, int z, BlockState state)")
    fun setFullBlockId(x: Int, y: Int, z: Int, fullId: Int): Boolean

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "setBlockStateAtLayer(int x, int y, int z, int layer, BlockState state)")
    fun setFullBlockId(x: Int, y: Int, z: Int, layer: Int, fullId: Int): Boolean

    @PowerNukkitOnly
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int): Boolean
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int): Boolean

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    fun setBlock(x: Int, y: Int, z: Int, blockId: Int, meta: Int): Boolean

    @Deprecated
    @DeprecationDetails(reason = "The data is limited to 32 bits", replaceWith = "getBlockState", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    fun setBlockAtLayer(x: Int, y: Int, z: Int, layer: Int, blockId: Int, meta: Int): Boolean
    fun getBlockSkyLight(x: Int, y: Int, z: Int): Int
    fun setBlockSkyLight(x: Int, y: Int, z: Int, level: Int)
    fun getBlockLight(x: Int, y: Int, z: Int): Int
    fun setBlockLight(x: Int, y: Int, z: Int, level: Int)
    val skyLightArray: ByteArray?
    val lightArray: ByteArray?
    val isEmpty: Boolean

    @Since("1.4.0.0-PN")
    fun writeTo(stream: BinaryStream?)

    @get:PowerNukkitOnly
    val maximumLayer: Int

    @PowerNukkitOnly
    @Nonnull
    fun toNBT(): CompoundTag

    @Nonnull
    fun copy(): ChunkSection?

    // Does nothing
    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly("Needed for level backward compatibility")
    @set:Since("1.3.1.0-PN")
    @set:PowerNukkitOnly("Needed for level backward compatibility")
    var contentVersion: Int
        get() = 0
        set(contentVersion) {
            // Does nothing
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasBlocks(): Boolean {
        return !isEmpty
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockStateAtLayer(x: Int, y: Int, z: Int, layer: Int, state: BlockState?): Boolean

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockState(x: Int, y: Int, z: Int, state: BlockState?): Boolean {
        return setBlockStateAtLayer(x, y, z, 0, state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockChangeStateAbove(x: Int, y: Int, z: Int): Int

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun delayPaletteUpdates() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun scanBlocks(provider: LevelProvider, offsetX: Int, offsetZ: Int, min: BlockVector3, max: BlockVector3, condition: BiPredicate<BlockVector3?, BlockState?>): List<Block?>? {
        val offsetY = y shl 4
        val results: List<Block> = ArrayList()
        val current = BlockVector3()
        val minX: Int = Math.max(0, min.x - offsetX)
        val minY: Int = Math.max(0, min.y - offsetY)
        val minZ: Int = Math.max(0, min.z - offsetZ)
        for (x in Math.min(max.x - offsetX, 15) downTo minX) {
            current.x = offsetX + x
            for (z in Math.min(max.z - offsetZ, 15) downTo minZ) {
                current.z = offsetZ + z
                for (y in Math.min(max.y - offsetY, 15) downTo minY) {
                    current.y = offsetY + y
                    val state: BlockState = getBlockState(x, y, z)
                    if (condition.test(current, state)) {
                        results.add(state.getBlockRepairing(provider.getLevel(), current, 0))
                    }
                }
            }
        }
        return results
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun compressStorageLayers() {
    }
}