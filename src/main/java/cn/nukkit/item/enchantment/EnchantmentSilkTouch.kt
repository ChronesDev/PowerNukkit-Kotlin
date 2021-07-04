package cn.nukkit.item.enchantment

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentSilkTouch : Enchantment(ID_SILK_TOUCH, "untouching", Rarity.VERY_RARE, EnchantmentType.DIGGER) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 15
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return super.getMinEnchantAbility(level) + 50
    }

    @Override
    override fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== ID_FORTUNE_DIGGING
    }

    @Override
    override fun canEnchant(item: Item): Boolean {
        return item.isShears() || super.canEnchant(item)
    }

    @Override
    override fun isItemAcceptable(item: Item): Boolean {
        return if (item.isShears()) {
            true
        } else super.isItemAcceptable(item)
    }
}