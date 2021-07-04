package cn.nukkit.item.enchantment.trident

import cn.nukkit.item.enchantment.Enchantment

class EnchantmentTridentLoyalty : EnchantmentTrident(Enchantment.ID_TRIDENT_LOYALTY, "tridentLoyalty", Rarity.UNCOMMON) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 7 * level + 5
    }

    @Override
    fun getMaxLevel(): Int {
        return 3
    }
}