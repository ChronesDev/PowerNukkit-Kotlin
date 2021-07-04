package cn.nukkit.level.biome.impl.river

import cn.nukkit.level.generator.populator.impl.WaterIcePopulator

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class FrozenRiverBiome : RiverBiome() {
    @get:Override
    override val name: String
        get() = "Frozen River"

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
    }
}