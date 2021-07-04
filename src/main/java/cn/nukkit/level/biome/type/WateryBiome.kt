package cn.nukkit.level.biome.type

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
abstract class WateryBiome : CoveredBiome() {
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getSurfaceDepth(y: Int): Int {
        return if (useNewRakNetSurfaceDepth()) {
            getSurfaceDepth(0, y, 0)
        } else 0
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else 0
        //doesn't matter, surface depth is 0
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getGroundDepth(y: Int): Int {
        return if (useNewRakNetGroundDepth()) {
            getGroundDepth(0, y, 0)
        } else 5
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else DIRT
    }
}