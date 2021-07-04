package cn.nukkit.level.biome.impl.mesa

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
//porktodo: this biome has much smaller and more frequent plateaus than the normal mesa plateau (which is all one giant one)
class MesaPlateauFMBiome : MesaPlateauFBiome() {
    @get:Override
    override val name: String
        get() = "Mesa Plateau F M"
}