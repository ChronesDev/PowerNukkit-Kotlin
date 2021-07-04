package cn.nukkit.level.biome.impl.swamp

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
//porktodo: this should be flat in most places, and only rise up in a few
class SwamplandMBiome : SwampBiome() {
    @get:Override
    override val name: String
        get() = "Swampland M"
}