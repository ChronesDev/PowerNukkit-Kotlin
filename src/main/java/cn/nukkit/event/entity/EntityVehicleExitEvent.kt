package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

class EntityVehicleExitEvent(entity: Entity?, vehicle: Entity) : EntityEvent(), Cancellable {
    private val vehicle: Entity
    fun getVehicle(): Entity {
        return vehicle
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.vehicle = vehicle
    }
}