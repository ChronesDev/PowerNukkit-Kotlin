package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSwordIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(IRON_SWORD, meta, count, "Iron Sword") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_IRON
    }

    @Override
    override fun isSword(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_IRON
    }

    @Override
    override fun getAttackDamage(): Int {
        return 6
    }
}