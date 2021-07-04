package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemMelon @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(MELON, meta, count, "Melon") {
    constructor(meta: Integer?) : this(meta, 1) {}
}