package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChestplateIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(IRON_CHESTPLATE, meta, count, "Iron Chestplate") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_IRON
    }

    @Override
    override fun isChestplate(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 6
    }

    @Override
    override fun getMaxDurability(): Int {
        return 241
    }
}