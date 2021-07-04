package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemTotem @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(TOTEM, meta, count, "Totem of Undying") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}