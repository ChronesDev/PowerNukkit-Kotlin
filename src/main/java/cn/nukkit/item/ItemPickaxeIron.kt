package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPickaxeIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(IRON_PICKAXE, meta, count, "Iron Pickaxe") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_IRON
    }

    @Override
    override fun isPickaxe(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_IRON
    }

    @Override
    override fun getAttackDamage(): Int {
        return 4
    }
}