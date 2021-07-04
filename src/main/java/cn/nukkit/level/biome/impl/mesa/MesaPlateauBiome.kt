package cn.nukkit.level.biome.impl.mesa

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class MesaPlateauBiome : MesaBiome() {
    @get:Override
    override val name: String
        get() = "Mesa Plateau"

    init {
        this.setBaseHeight(1.5f)
        this.setHeightVariation(0.025f)
        this.setMoundHeight(0)
    }
}