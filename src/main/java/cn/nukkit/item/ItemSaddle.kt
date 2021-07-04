package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemSaddle @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(SADDLE, meta, count, "Saddle") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}