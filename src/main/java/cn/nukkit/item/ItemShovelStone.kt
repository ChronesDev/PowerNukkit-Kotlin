package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemShovelStone @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(STONE_SHOVEL, meta, count, "Stone Shovel") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_STONE
    }

    @Override
    override fun isShovel(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_STONE
    }

    @Override
    override fun getAttackDamage(): Int {
        return 2
    }
}