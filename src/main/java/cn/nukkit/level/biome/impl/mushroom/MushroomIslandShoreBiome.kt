package cn.nukkit.level.biome.impl.mushroom

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class MushroomIslandShoreBiome : MushroomIslandBiome() {
    @get:Override
    override val name: String
        get() = "Mushroom Island Shore"

    init {
        this.setBaseHeight(0f)
        this.setHeightVariation(0.025f)
    }
}