package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChickenRaw @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(RAW_CHICKEN, meta, count, "Raw Chicken") {
    constructor(meta: Integer?) : this(meta, 1) {}
}