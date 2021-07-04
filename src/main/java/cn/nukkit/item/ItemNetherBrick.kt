package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemNetherBrick @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(NETHER_BRICK, meta, count, "Nether Brick") {
    constructor(meta: Integer?) : this(meta, 1) {}
}