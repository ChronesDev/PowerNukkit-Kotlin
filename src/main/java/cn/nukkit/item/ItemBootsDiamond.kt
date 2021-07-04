package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBootsDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(DIAMOND_BOOTS, meta, count, "Diamond Boots") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_DIAMOND
    }

    @Override
    override fun isBoots(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 430
    }

    @Override
    override fun getToughness(): Int {
        return 2
    }
}