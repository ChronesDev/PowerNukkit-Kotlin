package cn.nukkit.level.biome.impl.jungle

import cn.nukkit.level.biome.type.GrassyBiome

/**
 * @author DaPorkchop_
 */
class JungleBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Jungle"

    init {
        val trees = JungleTreePopulator()
        trees.setBaseAmount(10)
        this.addPopulator(trees)
        val bigTrees = JungleBigTreePopulator()
        bigTrees.setBaseAmount(6)
        this.addPopulator(bigTrees)
        val melon = PopulatorMelon()
        melon.setBaseAmount(-65)
        melon.setRandomAmount(70)
        this.addPopulator(melon)
    }
}