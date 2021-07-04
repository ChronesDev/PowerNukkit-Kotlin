package cn.nukkit.level.biome.type

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
abstract class SnowyBiome : GrassyBiome() {
    @get:RemovedFromNewRakNet
    @get:Override
    @get:Since("1.4.0.0-PN")
    override val coverBlock: Int
        get() = if (useNewRakNetCover()) {
            getCoverId(0, 0)
        } else SNOW_LAYER

    @Override
    fun canRain(): Boolean {
        return false
    }

    init {
        val waterIce = WaterIcePopulator()
        this.addPopulator(waterIce)
    }
}