package cn.nukkit.level.biome.impl.iceplains

import cn.nukkit.block.BlockSapling

/**
 * @author MagicDroidX (Nukkit Project)
 */
class IcePlainsBiome : SnowyBiome() {
    val name: String
        get() = "Ice Plains"

    init {
        val trees = PopulatorTree(BlockSapling.SPRUCE)
        trees.setBaseAmount(0)
        trees.setRandomAmount(1)
        this.addPopulator(trees)
        this.setBaseHeight(0.125f)
        this.setHeightVariation(0.05f)
    }
}