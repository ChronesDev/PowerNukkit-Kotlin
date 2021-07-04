package cn.nukkit.event.inventory

import cn.nukkit.entity.item.EntityItem

/**
 * @author MagicDroidX (Nukkit Project)
 */
class InventoryPickupItemEvent(inventory: Inventory, item: EntityItem) : InventoryEvent(inventory), Cancellable {
    private val item: EntityItem
    fun getItem(): EntityItem {
        return item
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.item = item
    }
}