package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChestplateLeather @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemColorArmor(LEATHER_TUNIC, meta, count, "Leather Tunic") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_LEATHER
    }

    @Override
    override fun isChestplate(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 81
    }
}