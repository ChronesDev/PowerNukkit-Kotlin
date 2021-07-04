package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBread @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(BREAD, meta, count, "Bread") {
    constructor(meta: Integer?) : this(meta, 1) {}
}