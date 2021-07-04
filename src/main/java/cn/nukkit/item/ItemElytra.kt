package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemElytra @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(ELYTRA, meta, count, "Elytra") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxDurability(): Int {
        return 431
    }

    @Override
    override fun isArmor(): Boolean {
        return true
    }

    @Override
    override fun isChestplate(): Boolean {
        return true
    }
}