package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Leonidius20
 * @since 18.08.18
 */
class ItemGhastTear @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GHAST_TEAR, meta, count, "Ghast Tear") {
    constructor(meta: Integer?) : this(meta, 1) {}
}