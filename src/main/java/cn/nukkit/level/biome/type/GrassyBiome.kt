package cn.nukkit.level.biome.type

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class GrassyBiome : CoveredBiome() {
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getSurfaceBlock(y: Int): Int {
        return if (useNewRakNetSurface()) {
            getSurfaceId(0, y, 0)
        } else GRASS
    }

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    override fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else DIRT
    }

    init {
        val grass = PopulatorGrass()
        grass.setBaseAmount(30)
        this.addPopulator(grass)
        val tallGrass = PopulatorDoublePlant(BlockDoublePlant.TALL_GRASS)
        tallGrass.setBaseAmount(5)
        this.addPopulator(tallGrass)
    }
}