package cn.nukkit.level.biome.type

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class SandyBiome : CoveredBiome() {
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getSurfaceDepth(y: Int): Int {
        return if (useNewRakNetSurfaceDepth()) {
            getSurfaceDepth(0, y, 0)
        } else 3
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0) shr 4
        } else SAND
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getGroundDepth(y: Int): Int {
        return if (useNewRakNetGroundDepth()) {
            getGroundDepth(0, y, 0)
        } else 2
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else SANDSTONE
    }
}