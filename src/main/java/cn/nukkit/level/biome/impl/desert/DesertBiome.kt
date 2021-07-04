package cn.nukkit.level.biome.impl.desert

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class DesertBiome : SandyBiome() {
    @get:Override
    val name: String
        get() = "Desert"

    @Override
    fun canRain(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val isDry: Boolean
        get() = true

    init {
        val cactus = PopulatorCactus()
        cactus.setBaseAmount(2)
        this.addPopulator(cactus)
        val deadbush = PopulatorDeadBush()
        deadbush.setBaseAmount(2)
        this.addPopulator(deadbush)
        this.setBaseHeight(0.125f)
        this.setHeightVariation(0.05f)
    }
}