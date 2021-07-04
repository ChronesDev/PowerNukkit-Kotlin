package cn.nukkit.event.inventory

import cn.nukkit.entity.projectile.EntityArrow

/**
 * @author MagicDroidX (Nukkit Project)
 */
class InventoryPickupArrowEvent(inventory: Inventory, arrow: EntityArrow) : InventoryEvent(inventory), Cancellable {
    private val arrow: EntityArrow
    fun getArrow(): EntityArrow {
        return arrow
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.arrow = arrow
    }
}