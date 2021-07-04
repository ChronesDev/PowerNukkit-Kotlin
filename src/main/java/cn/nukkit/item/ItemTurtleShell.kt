package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author PetteriM1
 */
class ItemTurtleShell @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemArmor(TURTLE_SHELL, meta, count, "Turtle Shell") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_OTHER
    }

    @Override
    override fun isHelmet(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 2
    }

    @Override
    override fun getMaxDurability(): Int {
        return 276
    }

    @Override
    override fun getToughness(): Int {
        return 2
    }
}