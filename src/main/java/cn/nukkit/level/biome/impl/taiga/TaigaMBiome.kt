package cn.nukkit.level.biome.impl.taiga

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
//porktodo: this should be flat-ish in most places, and upheavals should be steep
class TaigaMBiome : TaigaBiome() {
    @get:Override
    override val name: String
        get() = "Taiga M"

    init {
        this.setBaseHeight(0.3f)
        this.setHeightVariation(0.4f)
    }
}