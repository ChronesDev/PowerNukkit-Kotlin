package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author PetteriM1
 */
class ItemDriedKelp @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(DRIED_KELP, 0, count, "Dried Kelp") {
    constructor(meta: Integer?) : this(meta, 1) {}
}