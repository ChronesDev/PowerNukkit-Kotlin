package cn.nukkit.item.enchantment.damage

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentDamageSmite : EnchantmentDamage(ID_DAMAGE_SMITE, "undead", Rarity.UNCOMMON, TYPE.SMITE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 5 + (level - 1) * 8
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 20
    }

    @Override
    fun getDamageBonus(entity: Entity?): Double {
        return if (entity is EntitySmite) {
            getLevel() * 2.5
        } else 0
    }
}