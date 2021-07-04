package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemEmptyMap @JvmOverloads constructor(meta: Integer = 0, count: Int = 1) : Item(EMPTY_MAP, meta, count, "Empty Map") {
    constructor(meta: Integer) : this(meta, 1) {}

    init {
        if (meta == 2) {
            this.name = "Empty Locator Map"
        }
    }
}