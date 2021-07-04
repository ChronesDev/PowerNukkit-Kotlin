package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemHelmetDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(DIAMOND_HELMET, meta, count, "Diamond Helmet") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_DIAMOND
    }

    @Override
    override fun isHelmet(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 364
    }

    @Override
    override fun getToughness(): Int {
        return 2
    }
}