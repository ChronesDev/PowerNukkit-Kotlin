package cn.nukkit.level.biome.impl.desert

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class DesertHillsBiome : DesertBiome() {
    @get:Override
    override val name: String
        get() = "Desert Hills"

    init {
        this.setBaseHeight(0.45f)
        this.setHeightVariation(0.3f)
    }
}