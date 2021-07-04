package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemHorseArmorGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(GOLD_HORSE_ARMOR, meta, count, "Gold Horse Armor") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}