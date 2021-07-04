package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class ItemClownfish @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemFish(CLOWNFISH, meta, count, "Clownfish") {
    constructor(meta: Integer?) : this(meta, 1) {}
}