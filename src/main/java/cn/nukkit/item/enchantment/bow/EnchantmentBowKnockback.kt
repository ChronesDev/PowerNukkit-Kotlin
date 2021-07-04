package cn.nukkit.item.enchantment.bow

import cn.nukkit.item.enchantment.Enchantment

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentBowKnockback : EnchantmentBow(Enchantment.ID_BOW_KNOCKBACK, "arrowKnockback", Rarity.RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 12 + (level - 1) * 20
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 25
    }

    @Override
    fun getMaxLevel(): Int {
        return 2
    }
}