package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemHelmetLeather @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemColorArmor(LEATHER_CAP, meta, count, "Leather Cap") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_LEATHER
    }

    @Override
    override fun isHelmet(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 1
    }

    @Override
    override fun getMaxDurability(): Int {
        return 56
    }
}