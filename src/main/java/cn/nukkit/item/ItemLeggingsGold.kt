package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemLeggingsGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(GOLD_LEGGINGS, meta, count, "Gold Leggings") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_GOLD
    }

    @Override
    override fun isLeggings(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 106
    }
}