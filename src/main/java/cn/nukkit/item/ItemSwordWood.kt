package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSwordWood @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(WOODEN_SWORD, meta, count, "Wooden Sword") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_WOODEN
    }

    @Override
    override fun isSword(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_WOODEN
    }

    @Override
    override fun getAttackDamage(): Int {
        return 4
    }
}