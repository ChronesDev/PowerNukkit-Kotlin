package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemEnderEye @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(ENDER_EYE, meta, count, "Ender Eye") {
    constructor(meta: Integer?) : this(meta, 1) {}
}