package cn.nukkit.level.biome.impl.plains

import cn.nukkit.block.BlockDoublePlant

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class SunflowerPlainsBiome : PlainsBiome() {
    @get:Override
    override val name: String
        get() = "Sunflower Plains"

    init {
        val sunflower = PopulatorDoublePlant(BlockDoublePlant.SUNFLOWER)
        sunflower.setBaseAmount(8)
        sunflower.setRandomAmount(5)
        this.addPopulator(sunflower)
    }
}