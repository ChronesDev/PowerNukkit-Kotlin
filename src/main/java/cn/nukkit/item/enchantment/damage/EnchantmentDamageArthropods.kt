package cn.nukkit.item.enchantment.damage

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentDamageArthropods : EnchantmentDamage(ID_DAMAGE_ARTHROPODS, "arthropods", Rarity.UNCOMMON, TYPE.SMITE) {
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
        return if (entity is EntityArthropod) {
            getLevel() * 2.5
        } else 0
    }

    @Override
    fun doPostAttack(attacker: Entity?, entity: Entity) {
        if (entity is EntityArthropod) {
            val duration: Int = 20 + ThreadLocalRandom.current().nextInt(10 * this.level)
            entity.addEffect(Effect.getEffect(Effect.SLOWNESS).setDuration(duration).setAmplifier(3))
        }
    }
}