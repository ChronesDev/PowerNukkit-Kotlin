package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemPrismarineCrystals @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(PRISMARINE_CRYSTALS, 0, count, "Prismarine Crystals") {
    constructor(meta: Integer?) : this(meta, 1) {}
}