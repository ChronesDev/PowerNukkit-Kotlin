package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBookEnchanted @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(ENCHANTED_BOOK, meta, count, "Enchanted Book") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}