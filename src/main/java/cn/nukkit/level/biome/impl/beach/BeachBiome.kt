package cn.nukkit.level.biome.impl.beach

import cn.nukkit.level.biome.type.SandyBiome

/**
 * @author PeratX (Nukkit Project)
 */
class BeachBiome : SandyBiome() {
    @get:Override
    val name: String
        get() = "Beach"

    init {
        val sugarcane = PopulatorSugarcane()
        sugarcane.setBaseAmount(0)
        sugarcane.setRandomAmount(3)
        this.addPopulator(sugarcane)
        this.setBaseHeight(0f)
        this.setHeightVariation(0.025f)
    }
}