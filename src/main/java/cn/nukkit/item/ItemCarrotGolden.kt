package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemCarrotGolden @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(GOLDEN_CARROT, 0, count, "Golden Carrot") {
    constructor(meta: Integer?) : this(meta, 1) {}
}