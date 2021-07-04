package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPumpkinPie @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(PUMPKIN_PIE, meta, count, "Pumpkin Pie") {
    constructor(meta: Integer?) : this(meta, 1) {}
}