package cn.nukkit.event.inventory

import cn.nukkit.Player

@Since("1.4.0.0-PN")
class RepairItemEvent @Since("1.4.0.0-PN") constructor(inventory: AnvilInventory, oldItem: Item, newItem: Item, materialItem: Item, cost: Int, player: Player) : InventoryEvent(inventory), Cancellable {
    private val oldItem: Item
    private val newItem: Item
    private val materialItem: Item
    var cost: Int
        @Since("1.4.0.0-PN") get
        @Since("1.4.0.0-PN") set
    private val player: Player
    @Since("1.4.0.0-PN")
    fun getOldItem(): Item {
        return oldItem
    }

    @Since("1.4.0.0-PN")
    fun getNewItem(): Item {
        return newItem
    }

    @Since("1.4.0.0-PN")
    fun getMaterialItem(): Item {
        return materialItem
    }

    @Since("1.4.0.0-PN")
    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
            @Since("1.4.0.0-PN") get
    }

    init {
        this.oldItem = oldItem
        this.newItem = newItem
        this.materialItem = materialItem
        this.cost = cost
        this.player = player
    }
}