package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemFishCooked @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemFish(COOKED_FISH, meta, count, "Cooked Fish") {
    constructor(meta: Integer?) : this(meta, 1) {}
}