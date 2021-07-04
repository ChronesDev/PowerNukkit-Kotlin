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
 * very steep (1-2 block at a time) hills with round tops. flat in between
 */
class ExtremeHillsPlusMBiome : ExtremeHillsMBiome(false) {
    @get:Override
    override val name: String
        get() = "Extreme Hills+ M"

    @Override
    override fun doesOverhang(): Boolean {
        return false
    }

    init {
        this.setBaseHeight(1f)
        this.setHeightVariation(0.5f)
    }
}