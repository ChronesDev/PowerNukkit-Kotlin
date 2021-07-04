package cn.nukkit.level.biome.impl.forest

import cn.nukkit.block.BlockDoublePlant

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class FlowerForestBiome @JvmOverloads constructor(type: Int = TYPE_NORMAL) : ForestBiome(type) {
    @get:Override
    override val name: String
        get() = if (this.type === TYPE_BIRCH) "Birch Forest" else "Forest"

    init {

        //see https://minecraft.gamepedia.com/Flower#Flower_biomes
        val flower = PopulatorFlower()
        flower.setBaseAmount(10)
        flower.addType(DANDELION, 0)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_POPPY)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_ALLIUM)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_AZURE_BLUET)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_RED_TULIP)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_ORANGE_TULIP)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_WHITE_TULIP)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_PINK_TULIP)
        flower.addType(RED_FLOWER, BlockFlower.TYPE_OXEYE_DAISY)
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.LILAC)
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.ROSE_BUSH)
        flower.addType(DOUBLE_PLANT, BlockDoublePlant.PEONY)
        this.addPopulator(flower)
        this.setHeightVariation(0.4f)
    }
}