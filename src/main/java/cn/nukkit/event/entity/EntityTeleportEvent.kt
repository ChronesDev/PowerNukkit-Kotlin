package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityTeleportEvent(entity: Entity?, from: Location, to: Location) : EntityEvent(), Cancellable {
    private var from: Location
    private var to: Location
    fun getFrom(): Location {
        return from
    }

    fun setFrom(from: Location) {
        this.from = from
    }

    fun getTo(): Location {
        return to
    }

    fun setTo(to: Location) {
        this.to = to
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.from = from
        this.to = to
    }
}