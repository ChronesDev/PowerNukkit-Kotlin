package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPickaxeDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(DIAMOND_PICKAXE, meta, count, "Diamond Pickaxe") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_DIAMOND
    }

    @Override
    override fun isPickaxe(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_DIAMOND
    }

    @Override
    override fun getAttackDamage(): Int {
        return 5
    }
}