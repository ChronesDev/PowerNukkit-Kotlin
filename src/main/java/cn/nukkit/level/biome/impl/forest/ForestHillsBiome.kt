package cn.nukkit.level.biome.impl.forest

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class ForestHillsBiome @JvmOverloads constructor(type: Int = TYPE_NORMAL) : ForestBiome(type) {
    @get:Override
    override val name: String
        get() = when (this.type) {
            TYPE_BIRCH -> "Birch Forest Hills"
            TYPE_BIRCH_TALL -> "Birch Forest Hills M"
            else -> "Forest Hills"
        }

    init {
        this.setBaseHeight(0.45f)
        this.setHeightVariation(0.3f)
    }
}