package cn.nukkit.level.biome.impl.taiga

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class MegaTaigaHillsBiome : MegaTaigaBiome() {
    @get:Override
    override val name: String
        get() = "Mega Taiga Hills"

    init {
        this.setBaseHeight(0.45f)
        this.setHeightVariation(0.3f)
    }
}