package cn.nukkit.level.biome.impl.taiga

import cn.nukkit.level.generator.populator.impl.tree.SpruceBigTreePopulator

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class MegaTaigaBiome : TaigaBiome() {
    @get:Override
    override val name: String
        get() = "Mega Taiga"

    init {
        val bigTrees = SpruceBigTreePopulator()
        bigTrees.setBaseAmount(6)
        this.addPopulator(bigTrees)
        this.setBaseHeight(0.2f)
        this.setHeightVariation(0.2f)
    }
}