package cn.nukkit.level.biome.impl.mesa

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
class MesaBryceBiome : MesaBiome() {
    @get:Override
    override val name: String
        get() = "Mesa (Bryce)"

    @get:Override
    protected override val moundFrequency: Float
        protected get() = 1 / 16f

    @Override
    protected override fun minHill(): Float {
        return 0.3f
    }
}