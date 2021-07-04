package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemGunpowder @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GUNPOWDER, meta, count, "Gunpowder") {
    constructor(meta: Integer?) : this(meta, 1) {}
}