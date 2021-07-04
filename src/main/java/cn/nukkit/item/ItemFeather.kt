package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemFeather @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(FEATHER, 0, count, "Feather") {
    constructor(meta: Integer?) : this(meta, 1) {}
}