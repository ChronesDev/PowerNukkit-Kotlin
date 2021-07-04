package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentWaterBreath : Enchantment(ID_WATER_BREATHING, "oxygen", Rarity.RARE, EnchantmentType.ARMOR_HEAD) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 10 * level
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 30
    }

    @Override
    override fun getMaxLevel(): Int {
        return 3
    }
}