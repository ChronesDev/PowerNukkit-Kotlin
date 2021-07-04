package cn.nukkit.item.enchantment.protection

import cn.nukkit.event.entity.EntityDamageEvent

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentProtectionFall : EnchantmentProtection(ID_PROTECTION_FALL, "fall", Rarity.UNCOMMON, TYPE.FALL) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 5 + (level - 1) * 6
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 6
    }

    @Override
    override fun getTypeModifier(): Double {
        return 2
    }

    @Override
    fun getProtectionFactor(e: EntityDamageEvent): Float {
        val cause: DamageCause = e.getCause()
        return if (level <= 0 || cause !== DamageCause.FALL) {
            0
        } else (getLevel() * getTypeModifier())
    }
}