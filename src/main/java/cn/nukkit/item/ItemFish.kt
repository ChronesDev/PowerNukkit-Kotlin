package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemFish : ItemEdible {
    constructor(meta: Integer?) : this(meta, 1) {}

    @JvmOverloads
    constructor(meta: Integer? = 0, count: Int = 1) : super(RAW_FISH, meta, count, "Raw Fish") {
    }

    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {}
}