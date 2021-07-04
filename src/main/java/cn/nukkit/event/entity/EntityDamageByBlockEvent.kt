package cn.nukkit.event.entity

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityDamageByBlockEvent(damager: Block, entity: Entity?, cause: DamageCause?, damage: Float) : EntityDamageEvent(entity, cause, damage) {
    private val damager: Block
    fun getDamager(): Block {
        return damager
    }

    init {
        this.damager = damager
    }
}