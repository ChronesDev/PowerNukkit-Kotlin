package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemNetherStar @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(NETHER_STAR, 0, count, "Nether Star") {
    constructor(meta: Integer?) : this(meta, 1) {}
}