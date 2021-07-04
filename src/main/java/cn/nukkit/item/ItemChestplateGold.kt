package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChestplateGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(GOLD_CHESTPLATE, meta, count, "Gold Chestplate") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_GOLD
    }

    @Override
    override fun isChestplate(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 5
    }

    @Override
    override fun getMaxDurability(): Int {
        return 113
    }
}