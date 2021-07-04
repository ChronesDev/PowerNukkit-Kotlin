package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class ItemRabbitStew @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(RABBIT_STEW, meta, count, "Rabbit Stew") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}