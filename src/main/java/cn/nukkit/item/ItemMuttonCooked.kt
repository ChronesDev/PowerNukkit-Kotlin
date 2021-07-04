package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemMuttonCooked @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(COOKED_MUTTON, meta, count, "Cooked Mutton") {
    constructor(meta: Integer?) : this(meta, 1) {}
}