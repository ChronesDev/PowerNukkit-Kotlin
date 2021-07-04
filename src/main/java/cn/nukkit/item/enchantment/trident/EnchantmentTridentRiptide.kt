package cn.nukkit.item.enchantment.trident

import cn.nukkit.item.enchantment.Enchantment

class EnchantmentTridentRiptide : EnchantmentTrident(Enchantment.ID_TRIDENT_RIPTIDE, "tridentRiptide", Rarity.RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 7 * level + 10
    }

    @Override
    fun getMaxLevel(): Int {
        return 3
    }

    @Override
    fun checkCompatibility(enchantment: Enchantment): Boolean {
        return (super.checkCompatibility(enchantment)
                && enchantment.id !== Enchantment.ID_TRIDENT_LOYALTY && enchantment.id !== Enchantment.ID_TRIDENT_CHANNELING)
    }
}