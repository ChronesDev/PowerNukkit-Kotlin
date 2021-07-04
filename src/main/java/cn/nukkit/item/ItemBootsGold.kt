package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBootsGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(GOLD_BOOTS, meta, count, "Gold Boots") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_GOLD
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
        return 92
    }
}