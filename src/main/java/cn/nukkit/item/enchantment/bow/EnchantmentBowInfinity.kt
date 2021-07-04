package cn.nukkit.item.enchantment.bow

import cn.nukkit.item.enchantment.Enchantment

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentBowInfinity : EnchantmentBow(Enchantment.ID_BOW_INFINITY, "arrowInfinite", Rarity.VERY_RARE) {
    @Override
    protected fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== Enchantment.ID_MENDING
    }

    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 20
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}