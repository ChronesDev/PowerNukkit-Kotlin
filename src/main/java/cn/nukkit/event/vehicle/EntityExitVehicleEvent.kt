package cn.nukkit.event.vehicle

import cn.nukkit.Player

class EntityExitVehicleEvent(riding: cn.nukkit.entity.Entity, vehicle: Entity) : VehicleEvent(vehicle), Cancellable {
    private val riding: cn.nukkit.entity.Entity
    val entity: cn.nukkit.entity.Entity
        get() = riding
    val isPlayer: Boolean
        get() = riding is Player

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.riding = riding
    }
}