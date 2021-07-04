package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author xtypr
 * @since 2015/12/25
 */
class ItemExpBottle @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ProjectileItem(EXPERIENCE_BOTTLE, meta, count, "Bottle o' Enchanting") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getProjectileEntityType(): String {
        return "ThrownExpBottle"
    }

    @Override
    override fun getThrowForce(): Float {
        return 1f
    }
}