package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Nukkit Project Team
 */
class EnchantmentEntry(enchantments: Array<Enchantment>, cost: Int, randomName: String) {
    private val enchantments: Array<Enchantment>
    private val cost: Int
    private val randomName: String
    fun getEnchantments(): Array<Enchantment> {
        return enchantments
    }

    fun getCost(): Int {
        return cost
    }

    fun getRandomName(): String {
        return randomName
    }

    init {
        this.enchantments = enchantments
        this.cost = cost
        this.randomName = randomName
    }
}