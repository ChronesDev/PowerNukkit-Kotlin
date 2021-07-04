package cn.nukkit.level.biome.impl.taiga

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
//porktodo: this biome has very steep cliffs
class ColdTaigaMBiome : ColdTaigaBiome() {
    @get:Override
    override val name: String
        get() = "Cold Taiga M"

    @Override
    fun doesOverhang(): Boolean {
        return true
    }
}