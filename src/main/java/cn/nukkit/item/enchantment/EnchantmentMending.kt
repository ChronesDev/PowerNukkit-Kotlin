package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Rover656
 */
class EnchantmentMending : Enchantment(ID_MENDING, "mending", Rarity.RARE, EnchantmentType.BREAKABLE) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 25 * level
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 50
    }

    @Override
    override fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== ID_BOW_INFINITY
    }
}