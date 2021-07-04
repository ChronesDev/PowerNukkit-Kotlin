package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemHoeIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(IRON_HOE, meta, count, "Iron Hoe") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_IRON
    }

    @Override
    override fun isHoe(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_IRON
    }
}