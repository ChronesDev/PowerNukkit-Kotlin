package cn.nukkit.item.enchantment

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentFireAspect : Enchantment(ID_FIRE_ASPECT, "fire", Rarity.RARE, EnchantmentType.SWORD) {
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
        return 2
    }

    @Override
    override fun doPostAttack(attacker: Entity?, entity: Entity) {
        val duration: Int = Math.max(entity.fireTicks / 20, getLevel() * 4)
        val ev = EntityCombustByEntityEvent(attacker, entity, duration)
        Server.getInstance().getPluginManager().callEvent(ev)
        if (!ev.isCancelled()) {
            entity.setOnFire(ev.getDuration())
        }
    }
}