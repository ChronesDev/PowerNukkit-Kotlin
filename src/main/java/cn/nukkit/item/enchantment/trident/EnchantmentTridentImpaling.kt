package cn.nukkit.item.enchantment.trident

import cn.nukkit.entity.Entity

class EnchantmentTridentImpaling : EnchantmentTrident(Enchantment.ID_TRIDENT_IMPALING, "tridentImpaling", Rarity.RARE) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 8 * level - 7
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 20
    }

    @Override
    fun getMaxLevel(): Int {
        return 5
    }

    @Override
    fun getDamageBonus(entity: Entity): Double {
        return if (entity.isTouchingWater() || entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity)) {
            2.5 * getLevel()
        } else 0
    }
}