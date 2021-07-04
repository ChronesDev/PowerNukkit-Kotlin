package cn.nukkit.event.entity

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Is cancellable only in PowerNukkit")
class EntityDespawnEvent(entity: cn.nukkit.entity.Entity) : EntityEvent(), Cancellable {
    val type: Int
    val position: Position
        get() = this.entity.getPosition()
    val isCreature: Boolean
        get() = this.entity is EntityCreature
    val isHuman: Boolean
        get() = this.entity is EntityHuman
    val isProjectile: Boolean
        get() = this.entity is EntityProjectile
    val isVehicle: Boolean
        get() = this.entity is Entity
    val isItem: Boolean
        get() = this.entity is EntityItem

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        type = entity.getNetworkId()
    }
}