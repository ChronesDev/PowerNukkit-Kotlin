package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSteak @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(STEAK, meta, count, "Steak") {
    constructor(meta: Integer?) : this(meta, 1) {}
}