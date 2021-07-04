package cn.nukkit.item.enchantment

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentDurability : Enchantment(ID_DURABILITY, "durability", Rarity.UNCOMMON, EnchantmentType.BREAKABLE) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 5 + (level - 1) * 8
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return super.getMinEnchantAbility(level) + 50
    }

    @Override
    override fun getMaxLevel(): Int {
        return 3
    }

    @Override
    override fun canEnchant(item: Item): Boolean {
        return item.getMaxDurability() >= 0 || super.canEnchant(item)
    }

    @Override
    override fun isItemAcceptable(item: Item): Boolean {
        return if (!item.isNull() && item.getMaxDurability() !== -1 && !item.isUnbreakable()) {
            true
        } else super.isItemAcceptable(item)
    }

    companion object {
        fun negateDamage(item: Item, level: Int, random: Random): Boolean {
            return !(item.isArmor() && random.nextFloat() < 0.6f) && random.nextInt(level + 1) > 0
        }
    }
}