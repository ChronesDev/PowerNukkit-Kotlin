package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

class VehicleMoveEvent(vehicle: Entity, from: Location, to: Location) : VehicleEvent(vehicle) {
    private val from: Location
    private val to: Location
    fun getFrom(): Location {
        return from
    }

    fun getTo(): Location {
        return to
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.from = from
        this.to = to
    }
}