package cn.nukkit.item.enchantment.crossbow

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class EnchantmentCrossbowMultishot @Since("1.4.0.0-PN") constructor() : EnchantmentCrossbow(Enchantment.ID_CROSSBOW_MULTISHOT, "crossbowMultishot", Rarity.RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 20
    }

    @Override
    fun getMaxLevel(): Int {
        return 1
    }

    @Override
    fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== ID_CROSSBOW_PIERCING
    }
}