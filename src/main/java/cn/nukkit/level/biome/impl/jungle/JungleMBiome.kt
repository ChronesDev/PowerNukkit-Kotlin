package cn.nukkit.level.biome.impl.jungle

import cn.nukkit.level.generator.populator.impl.tree.JungleFloorPopulator

/**
 * @author DaPorkchop_
 */
class JungleMBiome : JungleBiome() {
    @get:Override
    override val name: String
        get() = "Jungle M"

    init {
        val floor = JungleFloorPopulator()
        floor.setBaseAmount(10)
        floor.setRandomAmount(5)
        this.addPopulator(floor)
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.4f)
    }
}