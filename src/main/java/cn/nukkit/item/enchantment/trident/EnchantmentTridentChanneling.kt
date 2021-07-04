package cn.nukkit.item.enchantment.trident

import cn.nukkit.item.enchantment.Enchantment

class EnchantmentTridentChanneling : EnchantmentTrident(Enchantment.ID_TRIDENT_CHANNELING, "tridentChanneling", Rarity.VERY_RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 25
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}