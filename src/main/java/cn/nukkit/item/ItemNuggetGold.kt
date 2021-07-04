package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemNuggetGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GOLD_NUGGET, meta, count, "Gold Nugget") {
    constructor(meta: Integer?) : this(meta, 1) {}
}