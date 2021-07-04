package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemQuartz @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(NETHER_QUARTZ, 0, count, "Nether Quartz") {
    constructor(meta: Integer?) : this(meta, 1) {}
}