package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class InventoryEvent(inventory: Inventory) : Event() {
    protected val inventory: Inventory
    fun getInventory(): Inventory {
        return inventory
    }

    val viewers: Array<Any>
        get() = inventory.getViewers().toArray(Player.EMPTY_ARRAY)

    init {
        this.inventory = inventory
    }
}