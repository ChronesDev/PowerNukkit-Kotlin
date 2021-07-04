package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

class VehicleUpdateEvent(vehicle: Entity) : VehicleEvent(vehicle) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}