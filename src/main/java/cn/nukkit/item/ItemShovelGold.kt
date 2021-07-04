package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemShovelGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(GOLD_SHOVEL, meta, count, "Gold Shovel") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_GOLD
    }

    @Override
    override fun isShovel(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_GOLD
    }

    @Override
    override fun getAttackDamage(): Int {
        return 1
    }
}