package cn.nukkit.item.enchantment.protection

import cn.nukkit.event.entity.EntityDamageEvent

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentProtectionProjectile : EnchantmentProtection(ID_PROTECTION_PROJECTILE, "projectile", Rarity.UNCOMMON, TYPE.PROJECTILE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 3 + (level - 1) * 6
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 6
    }

    @Override
    override fun getTypeModifier(): Double {
        return 3
    }

    @Override
    fun getProtectionFactor(e: EntityDamageEvent): Float {
        val cause: DamageCause = e.getCause()
        return if (level <= 0 || cause !== DamageCause.PROJECTILE) {
            0
        } else (getLevel() * getTypeModifier())
    }
}