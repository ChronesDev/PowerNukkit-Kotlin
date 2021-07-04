package cn.nukkit.item.enchantment.damage

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentDamageAll : EnchantmentDamage(ID_DAMAGE_ALL, "all", Rarity.COMMON, TYPE.ALL) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 1 + (level - 1) * 11
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 20
    }

    @Override
    fun getMaxEnchantableLevel(): Int {
        return 4
    }

    @Override
    fun getDamageBonus(entity: Entity?): Double {
        return if (this.getLevel() <= 0) {
            0
        } else 0.5 + getLevel() * 0.5
    }
}