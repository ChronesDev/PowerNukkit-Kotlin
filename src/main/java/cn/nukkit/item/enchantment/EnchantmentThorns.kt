package cn.nukkit.item.enchantment

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentThorns : Enchantment(ID_THORNS, "thorns", Rarity.VERY_RARE, EnchantmentType.ARMOR) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 10 + (level - 1) * 20
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return super.getMinEnchantAbility(level) + 50
    }

    @Override
    override fun getMaxLevel(): Int {
        return 3
    }

    @Override
    override fun doPostAttack(attacker: Entity, entity: Entity) {
        if (entity !is EntityHumanType) {
            return
        }
        val human: EntityHumanType = entity as EntityHumanType
        var thornsLevel = 0
        for (armor in human.getInventory().getArmorContents()) {
            val thorns: Enchantment = armor.getEnchantment(Enchantment.ID_THORNS)
            if (thorns != null) {
                thornsLevel = Math.max(thorns.getLevel(), thornsLevel)
            }
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        if (shouldHit(random, thornsLevel)) {
            attacker.attack(EntityDamageByEntityEvent(entity, attacker, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getDamage(random, level), 0f))
        }
    }

    @Override
    override fun canEnchant(@Nonnull item: Item): Boolean {
        return item !is ItemElytra && super.canEnchant(item)
    }

    @Override
    override fun isItemAcceptable(item: Item): Boolean {
        return if (item is ItemArmor) {
            item !is ItemElytra
        } else super.isItemAcceptable(item)
    }

    companion object {
        private fun shouldHit(random: ThreadLocalRandom, level: Int): Boolean {
            return level > 0 && random.nextFloat() < 0.15 * level
        }

        private fun getDamage(random: ThreadLocalRandom, level: Int): Int {
            return if (level > 10) level - 10 else random.nextInt(1, 5)
        }
    }
}