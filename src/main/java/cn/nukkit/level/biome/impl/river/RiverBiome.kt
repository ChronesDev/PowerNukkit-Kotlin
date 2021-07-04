package cn.nukkit.level.biome.impl.river

import cn.nukkit.level.biome.type.WateryBiome

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class RiverBiome : WateryBiome() {
    @get:Override
    val name: String
        get() = "River"

    init {
        this.setBaseHeight(-0.5f)
        this.setHeightVariation(0f)
    }
}