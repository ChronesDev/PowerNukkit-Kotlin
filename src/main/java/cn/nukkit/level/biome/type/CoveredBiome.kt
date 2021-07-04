package cn.nukkit.level.biome.type

import cn.nukkit.api.*

/**
 * @author DaPorkchop_ (Nukkit Project), joserobjr
 *
 *
 * A biome with ground covering
 *
 */
abstract class CoveredBiome : Biome() {
    ////// Backward compatibility flags //////
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var useNewRakNetCover: Boolean? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var useNewRakNetSurfaceDepth: Boolean? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var useNewRakNetSurface: Boolean? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var useNewRakNetGroundDepth: Boolean? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected var useNewRakNetGroundBlock: Boolean? = null

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Exposed lock object and removed from new-raknet and not used by PowerNukkit")
    @Since("1.4.0.0-PN")
    @RemovedFromNewRakNet
    val synchronizeCover: Object = Object()

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    @NewRakNetOnly
    fun getCoverId(x: Int, z: Int): Int {
        useNewRakNetCover = false
        return coverBlock shl 4
    }

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     *
     * @implNote Removed from new-raknet branch
     */
    @get:Since("1.4.0.0-PN")
    @get:RemovedFromNewRakNet
    val coverBlock: Int
        get() = if (useNewRakNetCover()) {
            getCoverId(0, 0)
        } else {
            AIR
        }

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getCoverState(x: Int, z: Int): BlockState {
        return if (useNewRakNetCover()) {
            val fullId = getCoverId(x, z)
            val blockId = fullId shr 4
            val storage = fullId and 15
            BlockState.of(blockId, storage)
        } else {
            BlockState.of(coverBlock)
        }
    }

    @NewRakNetOnly
    fun getSurfaceDepth(x: Int, y: Int, z: Int): Int {
        useNewRakNetSurfaceDepth = false
        return getSurfaceDepth(y)
    }

    /**
     * The amount of times the surface block should be used
     *
     *
     * If &lt; 0 bad things will happen!
     *
     *
     * @param y y
     * @return surface depth
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getSurfaceDepth(y: Int): Int {
        return if (useNewRakNetSurfaceDepth()) {
            getSurfaceDepth(0, y, 0)
        } else {
            1
        }
    }

    @NewRakNetOnly
    fun getSurfaceId(x: Int, y: Int, z: Int): Int {
        useNewRakNetSurface = false
        return getSurfaceBlock(y) shl 4 or (getSurfaceMeta(y) and 0xF)
    }

    /**
     * Between cover and ground
     *
     * @param y y
     * @return surface block
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else {
            AIR
        }
    }

    /**
     * The metadata of the surface block
     *
     * @param y y
     * @return surface meta
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getSurfaceMeta(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) and 0xF
        } else {
            0
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getSurfaceState(x: Int, y: Int, z: Int): BlockState {
        return if (useNewRakNetSurface()) {
            val fullId = getSurfaceId(x, y, z)
            val blockId = fullId shr 4
            val storage = fullId and 15
            BlockState.of(blockId, storage)
        } else {
            BlockState.of(getSurfaceBlock(y), getSurfaceMeta(y))
        }
    }

    @NewRakNetOnly
    fun getGroundDepth(x: Int, y: Int, z: Int): Int {
        useNewRakNetGroundDepth = false
        return getGroundDepth(y)
    }

    /**
     * The amount of times the ground block should be used
     *
     *
     * If &lt; 0 bad things will happen!
     *
     * @param y y
     * @return ground depth
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getGroundDepth(y: Int): Int {
        return if (useNewRakNetGroundDepth()) {
            getGroundDepth(0, y, 0)
        } else {
            4
        }
    }

    @NewRakNetOnly
    fun getGroundId(x: Int, y: Int, z: Int): Int {
        useNewRakNetGroundBlock = false
        return getGroundBlock(y) shl 4 or (getGroundMeta(y) and 0xF)
    }

    /**
     * Between surface and stone
     *
     * @param y y
     * @return ground block
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else {
            AIR
        }
    }

    /**
     * The metadata of the ground block
     * @param y y
     * @return ground meta
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun getGroundMeta(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) and 15
        } else {
            0
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getGroundState(x: Int, y: Int, z: Int): BlockState {
        return if (useNewRakNetGround()) {
            val fullId = getGroundId(x, y, z)
            val blockId = fullId shr 4
            val storage = fullId and 15
            BlockState.of(blockId, storage)
        } else {
            BlockState.of(getGroundBlock(y), getGroundMeta(y))
        }
    }

    /**
     * The block used as stone/below all other surface blocks
     *
     * @return stone block
     *
     * @implNote Removed from new-raknet branch
     */
    @get:Since("1.4.0.0-PN")
    @get:RemovedFromNewRakNet
    val stoneBlock: Int
        get() = STONE

    /**
     * Called before a new block column is covered. Biomes can update any relevant variables here before covering.
     *
     *
     * Biome covering is synchronized on the biome, so thread safety isn't an issue.
     *
     *
     * @param x x
     * @param z z
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    fun preCover(x: Int, z: Int) {
    }

    @NewRakNetOnly
    fun doCover(x: Int, z: Int, @Nonnull chunk: FullChunk) {
        val fullX: Int = chunk.getX() shl 4 or x
        val fullZ: Int = chunk.getZ() shl 4 or z
        preCover(fullX, fullZ)
        val coverState: BlockState = getCoverState(fullX, fullZ)
        var hasCovered = false
        var realY: Int
        //start one below build limit in case of cover blocks
        var y = 254
        while (y > 32) {
            if (chunk.getBlockState(x, y, z).equals(STATE_STONE)) {
                COVER@ if (!hasCovered) {
                    if (y >= Normal.seaHeight) {
                        chunk.setBlockState(x, y + 1, z, coverState)
                        val surfaceDepth = this.getSurfaceDepth(fullX, y, fullZ)
                        for (i in 0 until surfaceDepth) {
                            realY = y - i
                            if (chunk.getBlockState(x, realY, z).equals(STATE_STONE)) {
                                chunk.setBlockState(x, realY, z, getSurfaceState(fullX, realY, fullZ))
                            } else break@COVER
                        }
                        y -= surfaceDepth
                    }
                    val groundDepth = this.getGroundDepth(fullX, y, fullZ)
                    for (i in 0 until groundDepth) {
                        realY = y - i
                        if (chunk.getBlockState(x, realY, z).equals(STATE_STONE)) {
                            chunk.setBlockState(x, realY, z, getGroundState(fullX, realY, fullZ))
                        } else break@COVER
                    }
                    //don't take all of groundDepth away because we do y-- in the loop
                    y -= groundDepth - 1
                }
                hasCovered = true
            } else {
                if (hasCovered) {
                    //reset it if this isn't a valid stone block (allows us to place ground cover on top and below overhangs)
                    hasCovered = false
                }
            }
            y--
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun useNewRakNetCover(): Boolean {
        val useNewRakNet = useNewRakNetCover
        return useNewRakNet
                ?: attemptToUseNewRakNet(
                        Runnable { getCoverId(0, 0) },
                        Supplier<Boolean> { useNewRakNetCover },
                        Consumer<Boolean> { `val` -> useNewRakNetCover = `val` }
                )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun useNewRakNetSurfaceDepth(): Boolean {
        val useNewRakNet = useNewRakNetSurfaceDepth
        return useNewRakNet
                ?: attemptToUseNewRakNet(
                        Runnable { getSurfaceDepth(0, 0, 0) },
                        Supplier<Boolean> { useNewRakNetSurfaceDepth },
                        Consumer<Boolean> { `val` -> useNewRakNetSurfaceDepth = `val` }
                )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun useNewRakNetSurface(): Boolean {
        val useNewRakNet = useNewRakNetSurface
        return useNewRakNet
                ?: attemptToUseNewRakNet(
                        Runnable { getSurfaceId(0, 0, 0) },
                        Supplier<Boolean> { useNewRakNetSurface },
                        Consumer<Boolean> { `val` -> useNewRakNetSurface = `val` }
                )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun useNewRakNetGroundDepth(): Boolean {
        val useNewRakNet = useNewRakNetGroundDepth
        return useNewRakNet
                ?: attemptToUseNewRakNet(
                        Runnable { getGroundDepth(0, 0, 0) },
                        Supplier<Boolean> { useNewRakNetGroundDepth },
                        Consumer<Boolean> { `val` -> useNewRakNetGroundDepth = `val` }
                )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun useNewRakNetGround(): Boolean {
        val useNewRakNet = useNewRakNetGroundBlock
        return useNewRakNet
                ?: attemptToUseNewRakNet(
                        Runnable { getGroundId(0, 0, 0) },
                        Supplier<Boolean> { useNewRakNetGroundBlock },
                        Consumer<Boolean> { `val` -> useNewRakNetGroundBlock = `val` }
                )
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun attemptToUseNewRakNet(method: Runnable, flagGetter: Supplier<Boolean?>, flagSetter: Consumer<Boolean?>): Boolean {
        method.run()
        val useNewRak: Boolean = flagGetter.get()
        if (useNewRak != null) {
            return useNewRak
        }
        flagSetter.accept(true)
        return true
    }

    companion object {
        private val STATE_STONE: BlockState = BlockState.of(STONE)
    }
}