package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemLeggingsDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(DIAMOND_LEGGINGS, meta, count, "Diamond Leggings") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun isLeggings(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_DIAMOND
    }

    @Override
    override fun getArmorPoints(): Int {
        return 6
    }

    @Override
    override fun getMaxDurability(): Int {
        return 496
    }

    @Override
    override fun getToughness(): Int {
        return 2
    }
}