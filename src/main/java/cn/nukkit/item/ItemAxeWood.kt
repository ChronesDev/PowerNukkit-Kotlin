package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemAxeWood @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(WOODEN_AXE, meta, count, "Wooden Axe") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_WOODEN
    }

    @Override
    override fun isAxe(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_WOODEN
    }

    @Override
    override fun getAttackDamage(): Int {
        return 3
    }
}