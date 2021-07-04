package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityCombustEvent(combustee: Entity?, duration: Int) : EntityEvent(), Cancellable {
    var duration: Int

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = combustee
        this.duration = duration
    }
}