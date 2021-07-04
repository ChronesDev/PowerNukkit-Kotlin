package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemSuspiciousStew @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(SUSPICIOUS_STEW, meta, count, "Suspicious Stew") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}