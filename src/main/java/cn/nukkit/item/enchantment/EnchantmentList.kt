package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Nukkit Project Team
 */
class EnchantmentList(size: Int) {
    private val enchantments: Array<EnchantmentEntry?>

    /**
     * @param slot  The index of enchantment.
     * @param entry The given enchantment entry.
     * @return [EnchantmentList]
     */
    fun setSlot(slot: Int, entry: EnchantmentEntry?): EnchantmentList {
        enchantments[slot] = entry
        return this
    }

    fun getSlot(slot: Int): EnchantmentEntry? {
        return enchantments[slot]
    }

    fun getSize(): Int {
        return enchantments.size
    }

    init {
        enchantments = arrayOfNulls<EnchantmentEntry>(size)
    }
}