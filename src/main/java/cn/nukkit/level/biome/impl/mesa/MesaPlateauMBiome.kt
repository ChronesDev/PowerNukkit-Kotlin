package cn.nukkit.level.biome.impl.mesa

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

/**
 * @author DaPorkchop_
 */
//porktodo: the plateaus here are smaller and less frequent than in the normal counterpart (which is one giant plateau)
class MesaPlateauMBiome : MesaBiome() {
    @get:Override
    override val name: String
        get() = "Mesa Plateau M"

    @get:Override
    protected override val moundFrequency: Float
        protected get() = 1 / 50f

    @Override
    protected override fun minHill(): Float {
        return 0.1f
    }

    init {
        this.setMoundHeight(10)
    }
}