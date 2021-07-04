package cn.nukkit.level.biome.impl.beach

import cn.nukkit.api.RemovedFromNewRakNet

class ColdBeachBiome : SandyBiome() {
    @get:RemovedFromNewRakNet
    @get:Override
    @get:Since("1.4.0.0-PN")
    val coverBlock: Int
        get() = if (useNewRakNetCover()) {
            getCoverId(0, 0) shr 4
        } else SNOW_LAYER

    @get:Override
    val name: String
        get() = "Cold Beach"

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
        this.setBaseHeight(0f)
        this.setHeightVariation(0.025f)
    }
}