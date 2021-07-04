package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBowl @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BOWL, 0, count, "Bowl") {
    constructor(meta: Integer?) : this(meta, 1) {}
}