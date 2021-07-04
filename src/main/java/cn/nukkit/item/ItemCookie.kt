package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemCookie @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(COOKIE, meta, count, "Cookie") {
    constructor(meta: Integer?) : this(meta, 1) {}
}