package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBootsLeather @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemColorArmor(LEATHER_BOOTS, meta, count, "Leather Boots") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_LEATHER
    }

    @Override
    override fun isBoots(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 1
    }

    @Override
    override fun getMaxDurability(): Int {
        return 66
    }
}