package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBootsChain @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(CHAIN_BOOTS, meta, count, "Chainmail Boots") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_CHAIN
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
        return 196
    }
}