package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemApple @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(APPLE, 0, count, "Apple") {
    constructor(meta: Integer?) : this(meta, 1) {}
}