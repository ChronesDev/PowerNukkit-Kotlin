package cn.nukkit.level.biome.impl.taiga

import cn.nukkit.level.generator.populator.impl.tree.SpruceBigTreePopulator

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class MegaSpruceTaigaBiome : TaigaBiome() {
    @get:Override
    override val name: String
        get() = "Mega Spruce Taiga"

    init {
        val bigTrees = SpruceBigTreePopulator()
        bigTrees.setBaseAmount(6)
        this.addPopulator(bigTrees)
    }
}