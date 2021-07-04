package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author lion
 * @since 21.03.17
 */
class ItemCarrotOnAStick @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemTool(CARROT_ON_A_STICK, meta, count, "Carrot on a stick") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    @Override
    override fun getMaxDurability(): Int {
        return 25
    }
}