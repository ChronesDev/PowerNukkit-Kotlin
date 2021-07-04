package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

class VehicleCreateEvent(vehicle: Entity) : VehicleEvent(vehicle), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}