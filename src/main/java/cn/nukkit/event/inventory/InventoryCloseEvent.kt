package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * @author Box (Nukkit Project)
 */
class InventoryCloseEvent(inventory: Inventory, who: Player) : InventoryEvent(inventory) {
    private val who: Player
    val player: Player
        get() = who

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.who = who
    }
}