package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author lion
 * @since 21.03.17
 */
class ItemBlazeRod @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BLAZE_ROD, meta, count, "Blaze Rod") {
    constructor(meta: Integer?) : this(meta, 1) {}
}