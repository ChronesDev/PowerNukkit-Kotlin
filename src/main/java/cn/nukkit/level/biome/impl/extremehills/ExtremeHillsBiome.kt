package cn.nukkit.level.biome.impl.extremehills

import cn.nukkit.block.BlockSapling

/**
 * @author DaPorkchop_ (Nukkit Project)
 *
 *
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * steep mountains with flat areas between
 */
class ExtremeHillsBiome @JvmOverloads constructor(tree: Boolean = true) : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Extreme Hills"

    @Override
    fun doesOverhang(): Boolean {
        return true
    }

    init {
        val oreEmerald = PopulatorOreEmerald()
        this.addPopulator(oreEmerald)
        if (tree) {
            val trees = PopulatorTree(BlockSapling.SPRUCE)
            trees.setBaseAmount(2)
            trees.setRandomAmount(2)
            this.addPopulator(trees)
        }
        this.setBaseHeight(1f)
        this.setHeightVariation(0.5f)
    }
}