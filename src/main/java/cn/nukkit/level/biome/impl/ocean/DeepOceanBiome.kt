package cn.nukkit.level.biome.impl.ocean

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class DeepOceanBiome : OceanBiome() {
    @get:Override
    override val name: String
        get() = "Deep Ocean"

    init {

        //TODO: ocean monuments
        this.setBaseHeight(-1.8f)
        this.setHeightVariation(0.1f)
    }
}