package cn.nukkit.level.biome.impl.extremehills

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class ExtremeHillsEdgeBiome : ExtremeHillsBiome() {
    @get:Override
    override val name: String
        get() = "Extreme Hills Edge"

    init {
        this.setBaseHeight(0.8f)
        this.setHeightVariation(0.3f)
    }
}