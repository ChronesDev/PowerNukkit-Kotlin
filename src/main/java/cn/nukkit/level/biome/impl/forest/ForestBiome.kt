package cn.nukkit.level.biome.impl.forest

import cn.nukkit.block.BlockSapling

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ForestBiome @JvmOverloads constructor(val type: Int = TYPE_NORMAL) : GrassyBiome() {
    @get:Override
    val name: String
        get() = when (type) {
            TYPE_BIRCH -> "Birch Forest"
            TYPE_BIRCH_TALL -> "Birch Forest M"
            else -> "Forest"
        }

    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_BIRCH = 1
        const val TYPE_BIRCH_TALL = 2
    }

    init {
        var trees = PopulatorTree(if (type == TYPE_BIRCH_TALL) BlockSapling.BIRCH_TALL else BlockSapling.BIRCH)
        trees.setBaseAmount(if (type == TYPE_NORMAL) 3 else 6)
        this.addPopulator(trees)
        if (type == TYPE_NORMAL) {
            //normal forest biomes have both oak and birch trees
            trees = PopulatorTree(BlockSapling.OAK)
            trees.setBaseAmount(3)
            this.addPopulator(trees)
        }
    }
}