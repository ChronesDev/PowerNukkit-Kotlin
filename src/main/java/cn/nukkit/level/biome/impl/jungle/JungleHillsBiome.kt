package cn.nukkit.level.biome.impl.jungle

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class JungleHillsBiome : JungleBiome() {
    @get:Override
    override val name: String
        get() = "Jungle Hills"

    init {
        this.setBaseHeight(0.45f)
        this.setHeightVariation(0.3f)
    }
}