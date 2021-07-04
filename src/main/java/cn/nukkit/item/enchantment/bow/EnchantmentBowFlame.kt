package cn.nukkit.item.enchantment.bow

import cn.nukkit.item.enchantment.Enchantment

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentBowFlame : EnchantmentBow(Enchantment.ID_BOW_FLAME, "arrowFire", Rarity.RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 20
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}