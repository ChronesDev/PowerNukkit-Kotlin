package cn.nukkit.event.inventory

import cn.nukkit.Player

@Getter
@Setter
@Since("1.3.1.0-PN")
class EnchantItemEvent @Since("1.3.1.0-PN") constructor(inventory: EnchantInventory, oldItem: Item, newItem: Item, cost: Int, p: Player) : InventoryEvent(inventory), Cancellable {
    private val oldItem: Item
    private val newItem: Item
    private val xpCost: Int
    private val enchanter: Player

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.oldItem = oldItem
        this.newItem = newItem
        xpCost = cost
        enchanter = p
    }
}