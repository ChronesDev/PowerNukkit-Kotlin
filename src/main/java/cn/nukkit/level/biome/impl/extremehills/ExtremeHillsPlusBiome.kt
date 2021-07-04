package cn.nukkit.level.biome.impl.extremehills

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 *
 *
 * make sure this is touching another extreme hills type or it'll look dumb
 *
 * vertical cliffs, flat on top and on bottom
 */
class ExtremeHillsPlusBiome @JvmOverloads constructor(tree: Boolean = true) : ExtremeHillsBiome(tree) {
    @get:Override
    override val name: String
        get() = "Extreme Hills+"

    init {
        this.setBaseHeight(1f)
        this.setHeightVariation(0.5f)
    }
}