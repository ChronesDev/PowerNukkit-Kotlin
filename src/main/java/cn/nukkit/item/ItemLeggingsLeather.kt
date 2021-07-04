package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemLeggingsLeather @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemColorArmor(LEATHER_PANTS, meta, count, "Leather Pants") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_LEATHER
    }

    @Override
    override fun isLeggings(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 2
    }

    @Override
    override fun getMaxDurability(): Int {
        return 76
    }
}