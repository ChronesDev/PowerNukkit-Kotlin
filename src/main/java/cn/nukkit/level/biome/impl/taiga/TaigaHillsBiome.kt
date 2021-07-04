package cn.nukkit.level.biome.impl.taiga

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class TaigaHillsBiome : TaigaBiome() {
    @get:Override
    override val name: String
        get() = "Taiga Hills"

    init {
        this.setBaseHeight(0.25f)
        this.setHeightVariation(0.8f)
    }
}