package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBeefRaw @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(RAW_BEEF, meta, count, "Raw Beef") {
    constructor(meta: Integer?) : this(meta, 1) {}
}