package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSnowball @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ProjectileItem(SNOWBALL, 0, count, "Snowball") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }

    @Override
    override fun getProjectileEntityType(): String {
        return "Snowball"
    }

    @Override
    override fun getThrowForce(): Float {
        return 1.5f
    }
}