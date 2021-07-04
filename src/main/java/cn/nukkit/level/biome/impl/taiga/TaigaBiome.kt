package cn.nukkit.level.biome.impl.taiga

import cn.nukkit.block.BlockSapling

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class TaigaBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Taiga"

    init {
        val trees = PopulatorTree(BlockSapling.SPRUCE)
        trees.setBaseAmount(10)
        this.addPopulator(trees)
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.2f)
    }
}