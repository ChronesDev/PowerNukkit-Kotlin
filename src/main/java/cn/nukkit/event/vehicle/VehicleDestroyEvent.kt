package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

/**
 * Is called when an vehicle gets destroyed
 */
class VehicleDestroyEvent
/**
 * Constructor for the VehicleDestroyEvent
 *
 * @param vehicle the destroyed vehicle
 */
(vehicle: Entity) : VehicleEvent(vehicle), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}