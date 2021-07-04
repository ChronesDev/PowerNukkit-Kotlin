package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

/**
 * Is called when an entity damages a vehicle
 *
 * @author TrainmasterHD
 * @since 09.09.2019
 */
class VehicleDamageByEntityEvent(vehicle: EntityVehicle, attacker: Entity, damage: Double) : VehicleDamageEvent(vehicle, damage), Cancellable {
    private val attacker: Entity

    /**
     * Returns the attacking entity
     *
     * @return attacking entity
     */
    fun getAttacker(): Entity {
        return attacker
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    /**
     * Constructor for the VehicleDamageByEntityEvent
     *
     * @param vehicle  the damaged vehicle
     * @param attacker the attacking vehicle
     * @param damage   the caused damage on the vehicle
     */
    init {
        this.attacker = attacker
    }
}