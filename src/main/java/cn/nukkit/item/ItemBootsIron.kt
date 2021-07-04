package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBootsIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(IRON_BOOTS, meta, count, "Iron Boots") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_IRON
    }

    @Override
    override fun isBoots(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 2
    }

    @Override
    override fun getMaxDurability(): Int {
        return 196
    }
}