package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPaper @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(PAPER, meta, count, "Paper") {
    constructor(meta: Integer?) : this(meta, 1) {}
}