package cn.nukkit.level.biome.impl.savanna

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
//porktodo: this is just like savanna plateau with individual spikes
//see https://minecraft.gamepedia.com/Biome#Plateau_M
class SavannaPlateauMBiome : SavannaPlateauBiome() {
    @get:Override
    override val name: String
        get() = "Savanna Plateau M"

    @Override
    fun doesOverhang(): Boolean {
        return true
    }

    init {
        this.setBaseHeight(1.05f)
        this.setHeightVariation(1.2125001f)
    }
}