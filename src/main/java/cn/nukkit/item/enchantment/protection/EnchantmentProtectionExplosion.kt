package cn.nukkit.item.enchantment.protection

import cn.nukkit.event.entity.EntityDamageEvent

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentProtectionExplosion : EnchantmentProtection(ID_PROTECTION_EXPLOSION, "explosion", Rarity.RARE, TYPE.EXPLOSION) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 5 + (level - 1) * 8
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
        return if (level <= 0 || cause !== DamageCause.ENTITY_EXPLOSION && cause !== DamageCause.BLOCK_EXPLOSION) {
            0
        } else (getLevel() * getTypeModifier())
    }
}