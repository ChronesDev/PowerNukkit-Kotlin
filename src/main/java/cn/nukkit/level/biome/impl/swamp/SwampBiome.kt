package cn.nukkit.level.biome.impl.swamp

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class SwampBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Swamp"

    init {
        val lilypad = PopulatorLilyPad()
        lilypad.setBaseAmount(4)
        lilypad.setRandomAmount(2)
        this.addPopulator(lilypad)
        val trees = SwampTreePopulator()
        trees.setBaseAmount(2)
        this.addPopulator(trees)
        val flower = PopulatorFlower()
        flower.setBaseAmount(2)
        flower.addType(Block.RED_FLOWER, BlockFlower.TYPE_BLUE_ORCHID)
        this.addPopulator(flower)
        val mushroom = MushroomPopulator(1)
        mushroom.setBaseAmount(-5)
        mushroom.setRandomAmount(7)
        this.addPopulator(mushroom)
        val smallMushroom = PopulatorSmallMushroom()
        smallMushroom.setBaseAmount(0)
        smallMushroom.setRandomAmount(2)
        this.addPopulator(smallMushroom)
        this.setBaseHeight(-0.2f)
        this.setHeightVariation(0.1f)
    }
}