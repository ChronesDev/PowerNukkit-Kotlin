package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemPrismarineShard @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(PRISMARINE_SHARD, 0, count, "Prismarine Shard") {
    constructor(meta: Integer?) : this(meta, 1) {}
}