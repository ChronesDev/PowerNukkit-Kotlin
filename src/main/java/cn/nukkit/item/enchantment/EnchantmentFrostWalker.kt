package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class EnchantmentFrostWalker : Enchantment(ID_FROST_WALKER, "frostwalker", Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return level * 10
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 15
    }

    @Override
    override fun getMaxLevel(): Int {
        return 2
    }

    @Override
    protected override fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== ID_WATER_WALKER
    }
}