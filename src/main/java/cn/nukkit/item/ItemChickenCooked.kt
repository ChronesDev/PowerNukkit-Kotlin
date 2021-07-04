package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemChickenCooked @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(COOKED_CHICKEN, meta, count, "Cooked Chicken") {
    constructor(meta: Integer?) : this(meta, 1) {}
}