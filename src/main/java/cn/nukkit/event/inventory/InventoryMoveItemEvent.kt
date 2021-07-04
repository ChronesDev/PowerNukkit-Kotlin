package cn.nukkit.event.inventory

import cn.nukkit.event.Cancellable

/**
 * @author CreeperFace
 *
 *
 * Called when inventory transaction is not caused by a player
 */
class InventoryMoveItemEvent(from: Inventory, targetInventory: Inventory, source: InventoryHolder, item: Item, action: Action) : InventoryEvent(from), Cancellable {
    private val targetInventory: Inventory
    private val source: InventoryHolder
    private var item: Item
    val action: Action
    fun getTargetInventory(): Inventory {
        return targetInventory
    }

    fun getSource(): InventoryHolder {
        return source
    }

    fun getItem(): Item {
        return item
    }

    fun setItem(item: Item) {
        this.item = item
    }

    enum class Action {
        SLOT_CHANGE,  //transaction between 2 inventories
        PICKUP, DROP, DISPENSE
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.targetInventory = targetInventory
        this.source = source
        this.item = item
        this.action = action
    }
}