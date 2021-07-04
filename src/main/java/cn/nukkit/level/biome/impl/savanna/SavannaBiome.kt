package cn.nukkit.level.biome.impl.savanna

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author DaPorkchop_
 */
class SavannaBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Savanna"

    @Override
    fun canRain(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val isDry: Boolean
        get() = true

    init {
        val tree = SavannaTreePopulator(BlockSapling.ACACIA)
        tree.setBaseAmount(1)
        this.addPopulator(tree)
        this.setBaseHeight(0.125f)
        this.setHeightVariation(0.05f)
    }
}