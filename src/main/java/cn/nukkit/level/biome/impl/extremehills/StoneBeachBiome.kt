package cn.nukkit.level.biome.impl.extremehills

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_
 *
 *
 * Occurs when Extreme hills and variants touch the ocean.
 *
 * Nearly ertical cliffs, but no overhangs. Height difference is 2-7 near ocean, and pretty much flat everywhere else
 */
class StoneBeachBiome : CoveredBiome() {
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getSurfaceDepth(y: Int): Int {
        return if (useNewRakNetSurfaceDepth()) {
            getSurfaceDepth(0, y, 0)
        } else 0
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else 0
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getGroundDepth(y: Int): Int {
        return if (useNewRakNetGroundDepth()) {
            getGroundDepth(0, y, 0)
        } else 0
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else 0
    }

    @get:Override
    val name: String
        get() = "Stone Beach"

    init {
        this.setBaseHeight(0.1f)
        this.setHeightVariation(0.8f)
    }
}