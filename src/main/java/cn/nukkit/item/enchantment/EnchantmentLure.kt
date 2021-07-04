package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentLure : Enchantment(ID_LURE, "fishingSpeed", Rarity.RARE, EnchantmentType.FISHING_ROD) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return level + 8 * level + 6
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 45 + level
    }

    @Override
    override fun getMaxLevel(): Int {
        return 3
    }
}