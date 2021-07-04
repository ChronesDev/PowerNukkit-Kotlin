package cn.nukkit.level.biome.impl.jungle

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchpo_
 */
//porktodo: this biome has steep cliffs and flat plains
class JungleEdgeMBiome : JungleEdgeBiome() {
    @get:Override
    override val name: String
        get() = "Jungle Edge M"
}