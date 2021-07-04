package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Leonidius20
 * @since 18.08.18
 */
class ItemMelonGlistering(meta: Integer?, count: Int) : Item(GLISTERING_MELON, meta, count, "Glistering Melon") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(meta, 1) {
    }
}