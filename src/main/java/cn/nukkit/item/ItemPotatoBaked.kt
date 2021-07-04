package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPotatoBaked @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(BAKED_POTATO, meta, count, "Baked Potato") {
    constructor(meta: Integer?) : this(meta, 1) {}
}