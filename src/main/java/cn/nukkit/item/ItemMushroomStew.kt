package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemMushroomStew @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(MUSHROOM_STEW, 0, count, "Mushroom Stew") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}