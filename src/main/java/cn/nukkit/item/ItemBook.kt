package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBook @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BOOK, meta, count, "Book") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getEnchantAbility(): Int {
        return 1
    }
}