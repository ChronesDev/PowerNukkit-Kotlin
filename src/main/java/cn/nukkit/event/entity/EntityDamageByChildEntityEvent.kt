package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityDamageByChildEntityEvent(damager: Entity, childEntity: Entity, entity: Entity?, cause: DamageCause?, damage: Float) : EntityDamageByEntityEvent(damager, entity, cause, damage) {
    private val childEntity: Entity
    val child: cn.nukkit.entity.Entity
        get() = childEntity

    init {
        this.childEntity = childEntity
    }
}