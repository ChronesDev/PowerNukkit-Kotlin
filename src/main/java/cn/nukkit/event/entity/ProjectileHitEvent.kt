package cn.nukkit.event.entity

import cn.nukkit.entity.projectile.EntityProjectile

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ProjectileHitEvent(entity: EntityProjectile?, movingObjectPosition: MovingObjectPosition?) : EntityEvent(), Cancellable {
    private var movingObjectPosition: MovingObjectPosition?

    constructor(entity: EntityProjectile?) : this(entity, null) {}

    fun getMovingObjectPosition(): MovingObjectPosition? {
        return movingObjectPosition
    }

    fun setMovingObjectPosition(movingObjectPosition: MovingObjectPosition?) {
        this.movingObjectPosition = movingObjectPosition
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.movingObjectPosition = movingObjectPosition
    }
}