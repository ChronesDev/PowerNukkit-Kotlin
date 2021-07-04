package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChestplateDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_DIAMOND
    }

    @Override
    override fun isChestplate(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 8
    }

    @Override
    override fun getMaxDurability(): Int {
        return 529
    }

    @Override
    override fun getToughness(): Int {
        return 2
    }
}