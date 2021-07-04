package cn.nukkit.level.biome.impl.savanna

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class SavannaPlateauBiome : SavannaBiome() {
    @get:Override
    override val name: String
        get() = "Savanna Plateau"

    init {
        this.setBaseHeight(1.5f)
        this.setHeightVariation(0.025f)
    }
}