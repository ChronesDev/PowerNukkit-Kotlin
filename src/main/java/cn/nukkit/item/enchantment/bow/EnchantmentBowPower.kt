package cn.nukkit.item.enchantment.bow

import cn.nukkit.item.enchantment.Enchantment

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentBowPower : EnchantmentBow(Enchantment.ID_BOW_POWER, "arrowDamage", Rarity.COMMON) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 1 + (level - 1) * 10
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 15
    }

    @Override
    fun getMaxLevel(): Int {
        return 5
    }
}