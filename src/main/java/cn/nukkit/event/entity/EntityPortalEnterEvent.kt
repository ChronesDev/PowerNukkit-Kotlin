package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

class EntityPortalEnterEvent(entity: Entity?, type: PortalType) : EntityEvent(), Cancellable {
    val portalType: PortalType

    enum class PortalType {
        NETHER, END
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        portalType = type
    }
}