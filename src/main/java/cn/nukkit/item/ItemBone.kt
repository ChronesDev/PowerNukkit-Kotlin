package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBone @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BONE, meta, count, "Bone") {
    constructor(meta: Integer?) : this(meta, 1) {}
}