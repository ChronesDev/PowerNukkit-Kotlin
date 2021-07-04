package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPorkchopRaw @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(RAW_PORKCHOP, meta, count, "Raw Porkchop") {
    constructor(meta: Integer?) : this(meta, 1) {}
}