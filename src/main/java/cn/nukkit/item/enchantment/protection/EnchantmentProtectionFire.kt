package cn.nukkit.item.enchantment.protection

import cn.nukkit.event.entity.EntityDamageEvent

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentProtectionFire : EnchantmentProtection(ID_PROTECTION_FIRE, "fire", Rarity.UNCOMMON, TYPE.FIRE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 10 + (level - 1) * 8
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 8
    }

    @Override
    override fun getTypeModifier(): Double {
        return 2
    }

    @Override
    fun getProtectionFactor(e: EntityDamageEvent): Float {
        val cause: DamageCause = e.getCause()
        return if (level <= 0 || cause !== DamageCause.LAVA && cause !== DamageCause.FIRE && cause !== DamageCause.FIRE_TICK) {
            0
        } else (getLevel() * getTypeModifier())
    }
}