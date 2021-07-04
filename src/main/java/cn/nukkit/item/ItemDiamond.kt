package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemDiamond @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(DIAMOND, 0, count, "Diamond") {
    constructor(meta: Integer?) : this(meta, 1) {}
}