package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author joserobjr
 */
class ItemHoneycomb @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(HONEYCOMB, meta, count, "Honeycomb") {
    constructor(meta: Integer?) : this(meta, 1) {}
}