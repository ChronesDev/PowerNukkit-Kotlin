package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemGlowstoneDust @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GLOWSTONE_DUST, meta, count, "Glowstone Dust") {
    constructor(meta: Integer?) : this(meta, 1) {}
}