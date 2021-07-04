package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemNameTag @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(NAME_TAG, meta, count, "Name Tag") {
    constructor(meta: Integer?) : this(meta, 1) {}
}