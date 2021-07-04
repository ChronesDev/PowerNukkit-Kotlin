package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemShears @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(SHEARS, meta, count, "Shears") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_SHEARS
    }

    @Override
    override fun isShears(): Boolean {
        return true
    }
}