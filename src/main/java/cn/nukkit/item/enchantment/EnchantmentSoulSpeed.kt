package cn.nukkit.item.enchantment

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class EnchantmentSoulSpeed @Since("1.4.0.0-PN") constructor() : Enchantment(ID_SOUL_SPEED, "soul_speed", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 10 * level
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 15
    }

    @Override
    override fun getMaxLevel(): Int {
        return 3
    }
}