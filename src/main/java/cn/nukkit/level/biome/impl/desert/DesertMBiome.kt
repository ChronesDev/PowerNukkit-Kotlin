package cn.nukkit.level.biome.impl.desert

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class DesertMBiome : DesertBiome() {
    @get:Override
    override val name: String
        get() = "Desert M"

    init {
        this.setBaseHeight(0.225f)
        this.setHeightVariation(0.25f)
    }
}