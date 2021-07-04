package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Leonidius20
 * @since 18.08.18
 */
class ItemBlazePowder(meta: Integer?, count: Int) : Item(BLAZE_POWDER, meta, count, "Blaze Powder") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(meta, 1) {
    }
}