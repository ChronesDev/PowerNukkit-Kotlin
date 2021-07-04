package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

/**
 * @author larryTheCoder (Nukkit Project)
 * @since 7/5/2017
 */
abstract class VehicleEvent(vehicle: Entity) : Event() {
    private val vehicle: Entity
    fun getVehicle(): Entity {
        return vehicle
    }

    init {
        this.vehicle = vehicle
    }
}