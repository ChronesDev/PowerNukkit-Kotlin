package cn.nukkit.level.biome.impl.ocean

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author MagicDroidX (Nukkit Project)
 */
class OceanBiome : WateryBiome() {
    @get:Override
    val name: String
        get() = "Ocean"

    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    @Override
    fun getGroundBlock(y: Int): Int {
        return if (useNewRakNetGround()) {
            getGroundId(0, y, 0) shr 4
        } else GRAVEL
    }

    init {
        this.setBaseHeight(-1f)
        this.setHeightVariation(0.1f)
    }
}