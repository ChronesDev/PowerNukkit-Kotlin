package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentWaterWalker : Enchantment(ID_WATER_WALKER, "waterWalker", Rarity.RARE, EnchantmentType.ARMOR_FEET) {
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
        return 3
    }
}