package cn.nukkit.level.biome.impl.ocean

import cn.nukkit.level.generator.populator.impl.WaterIcePopulator

/**
 * @author DaPorkchop_ (Nukkit Project)
 *
 *
 * This biome does not generate naturally
 */
class FrozenOceanBiome : OceanBiome() {
    @get:Override
    override val name: String
        get() = "Frozen Ocean"

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