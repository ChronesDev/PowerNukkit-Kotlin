package cn.nukkit.event.inventory

import cn.nukkit.Player

/**
 * @author boybook (Nukkit Project)
 */
class InventoryClickEvent(who: Player, inventory: Inventory, val slot: Int, sourceItem: Item, heldItem: Item) : InventoryEvent(inventory), Cancellable {
    private val sourceItem: Item
    private val heldItem: Item
    private val player: Player
    fun getSourceItem(): Item {
        return sourceItem
    }

    fun getHeldItem(): Item {
        return heldItem
    }

    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.sourceItem = sourceItem
        this.heldItem = heldItem
        player = who
    }
}