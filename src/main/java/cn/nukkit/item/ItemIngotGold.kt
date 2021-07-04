package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemIngotGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GOLD_INGOT, 0, count, "Gold Ingot") {
    constructor(meta: Integer?) : this(meta, 1) {}
}