package cn.nukkit.item.enchantment

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentEfficiency : Enchantment(ID_EFFICIENCY, "digging", Rarity.COMMON, EnchantmentType.DIGGER) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 1 + (level - 1) * 10
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return super.getMinEnchantAbility(level) + 50
    }

    @Override
    override fun getMaxLevel(): Int {
        return 5
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