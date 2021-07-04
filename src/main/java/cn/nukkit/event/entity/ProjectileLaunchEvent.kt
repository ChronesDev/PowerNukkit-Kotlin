package cn.nukkit.event.entity

import cn.nukkit.entity.projectile.EntityProjectile

class ProjectileLaunchEvent(entity: EntityProjectile?) : EntityEvent(), Cancellable {
    override fun getEntity(): EntityProjectile {
        return this.entity as EntityProjectile
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
    }
}