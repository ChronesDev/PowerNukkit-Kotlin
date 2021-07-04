package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemClay @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(CLAY, meta, count, "Clay") {
    constructor(meta: Integer?) : this(meta, 1) {}
}