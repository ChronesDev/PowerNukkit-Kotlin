package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBrick @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BRICK, 0, count, "Brick") {
    constructor(meta: Integer?) : this(meta, 1) {}
}