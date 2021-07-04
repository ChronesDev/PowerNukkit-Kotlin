package cn.nukkit.level.biome.impl.plains

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class PlainsBiome : GrassyBiome() {
    @get:Override
    val name: String
        get() = "Plains"

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    val isDry: Boolean
        get() = true

    init {
        this.setBaseHeight(0.125f)
        this.setHeightVariation(0.05f)
    }
}