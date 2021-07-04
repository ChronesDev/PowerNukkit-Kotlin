package cn.nukkit.level.biome.impl.taiga

import cn.nukkit.api.RemovedFromNewRakNet

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class ColdTaigaBiome : TaigaBiome() {
    @get:Override
    override val name: String
        get() = "Cold Taiga"

    @get:RemovedFromNewRakNet
    @get:Override
    @get:Since("1.4.0.0-PN")
    val coverBlock: Int
        get() = if (useNewRakNetCover()) {
            getCoverId(0, 0) shr 4
        } else SNOW_LAYER

    @get:Override
    val isFreezing: Boolean
        get() = true

    @Override
    fun canRain(): Boolean {
        return false
    }

    init {
        val ice = WaterIcePopulator()
        this.addPopulator(ice)
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.2f)
    }
}