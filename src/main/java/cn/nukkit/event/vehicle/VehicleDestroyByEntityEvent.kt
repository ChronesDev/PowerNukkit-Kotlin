package cn.nukkit.event.vehicle

import cn.nukkit.entity.Entity

/**
 * Is called when an entity destroyed a vehicle
 *
 * @author TrainmasterHD
 * @since 09.09.2019
 */
class VehicleDestroyByEntityEvent(vehicle: Entity, destroyer: Entity) : VehicleDestroyEvent(vehicle), Cancellable {
    private val destroyer: Entity

    /**
     * Returns the destroying entity
     *
     * @return destroying entity
     */
    fun getDestroyer(): Entity {
        return destroyer
    }

    companion object {
        private val HANDLER_LIST: HandlerList = HandlerList()
        val handlers: HandlerList
            get() = HANDLER_LIST
    }

    /**
     * Constructor for the VehicleDestroyByEntityEvent
     *
     * @param vehicle   the destroyed vehicle
     * @param destroyer the destroying entity
     */
    init {
        this.destroyer = destroyer
    }
}