package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBeetrootSoup @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(BEETROOT_SOUP, 0, count, "Beetroot Soup") {
    constructor(meta: Integer?) : this(meta, 1) {}
}