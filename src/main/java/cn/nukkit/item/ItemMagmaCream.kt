package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Leonidius20
 * @since 18.08.18
 */
class ItemMagmaCream(meta: Integer?, count: Int) : Item(MAGMA_CREAM, meta, count, "Magma Cream") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(0, 1) {
    }
}