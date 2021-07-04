package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemEgg @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ProjectileItem(EGG, meta, count, "Egg") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getProjectileEntityType(): String {
        return "Egg"
    }

    @Override
    override fun getThrowForce(): Float {
        return 1.5f
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }
}