package cn.nukkit.item.enchantment.protection

import cn.nukkit.event.entity.EntityDamageEvent

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentProtectionAll : EnchantmentProtection(Enchantment.ID_PROTECTION_ALL, "all", Rarity.COMMON, TYPE.ALL) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 1 + (level - 1) * 11
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 11
    }

    @Override
    override fun getTypeModifier(): Double {
        return 1
    }

    @Override
    fun getProtectionFactor(e: EntityDamageEvent): Float {
        val cause: DamageCause = e.getCause()
        return if (level <= 0 || cause === DamageCause.VOID || cause === DamageCause.CUSTOM || cause === DamageCause.MAGIC) {
            0
        } else (getLevel() * getTypeModifier())
    }
}