package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentKnockback : Enchantment(ID_KNOCKBACK, "knockback", Rarity.UNCOMMON, EnchantmentType.SWORD) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 5 + (level - 1) * 20
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return super.getMinEnchantAbility(level) + 50
    }

    @Override
    override fun getMaxLevel(): Int {
        return 2
    }
}