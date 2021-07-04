package cn.nukkit.level.biome.impl.roofedforest

import cn.nukkit.level.biome.type.GrassyBiome

class RoofedForestBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Roofed Forest"

    init {
        val tree = DarkOakTreePopulator()
        tree.setBaseAmount(20)
        tree.setRandomAmount(10)
        this.addPopulator(tree)
        val flower = PopulatorFlower()
        flower.setBaseAmount(2)
        this.addPopulator(flower)
        val mushroom = MushroomPopulator()
        mushroom.setBaseAmount(0)
        mushroom.setRandomAmount(1)
        this.addPopulator(mushroom)
    }
}