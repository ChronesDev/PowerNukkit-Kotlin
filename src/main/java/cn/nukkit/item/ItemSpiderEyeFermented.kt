package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Leonidius20
 * @since 18.08.18
 */
class ItemSpiderEyeFermented(meta: Integer?, count: Int) : Item(FERMENTED_SPIDER_EYE, meta, count, "Fermented Spider Eye") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(0, 1) {
    }
}