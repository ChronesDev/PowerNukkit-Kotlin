package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemHelmetIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(IRON_HELMET, meta, count, "Iron Helmet") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_IRON
    }

    @Override
    override fun isHelmet(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 2
    }

    @Override
    override fun getMaxDurability(): Int {
        return 166
    }
}