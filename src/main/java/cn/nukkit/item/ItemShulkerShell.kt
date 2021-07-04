package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemShulkerShell @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(SHULKER_SHELL, meta, count, "Shulker Shell") {
    constructor(meta: Integer?) : this(meta, 1) {}
}