package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityCombustByEntityEvent(combuster: Entity, combustee: Entity?, duration: Int) : EntityCombustEvent(combustee, duration) {
    protected val combuster: Entity
    fun getCombuster(): Entity {
        return combuster
    }

    init {
        this.combuster = combuster
    }
}