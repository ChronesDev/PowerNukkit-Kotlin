package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemStick @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(STICK, 0, count, "Stick") {
    constructor(meta: Integer?) : this(meta, 1) {}
}