package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemMuttonRaw @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(RAW_MUTTON, meta, count, "Raw Mutton") {
    constructor(meta: Integer?) : this(meta, 1) {}
}