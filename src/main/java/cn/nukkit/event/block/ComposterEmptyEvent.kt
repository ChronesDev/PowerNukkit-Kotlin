package cn.nukkit.event.block

import cn.nukkit.Player

@PowerNukkitDifference(info = "The player and the item are null when they are empty by a hopper pulling the item", since = "1.4.0.0-PN")
class ComposterEmptyEvent(block: Block, player: Player, itemUsed: Item, drop: Item?, newLevel: Int) : BlockEvent(block), Cancellable {
    private val player: Player
    private var drop: Item?
    private var itemUsed: Item
    private var newLevel: Int
    var motion: Vector3? = null
    fun getPlayer(): Player {
        return player
    }

    fun getDrop(): Item {
        return drop.clone()
    }

    fun setDrop(drop: Item?) {
        var drop: Item? = drop
        drop = if (drop == null) {
            Item.get(Item.AIR)
        } else {
            drop.clone()
        }
        this.drop = drop
    }

    fun getItemUsed(): Item {
        return itemUsed
    }

    fun setItemUsed(itemUsed: Item) {
        this.itemUsed = itemUsed
    }

    fun getNewLevel(): Int {
        return newLevel
    }

    fun setNewLevel(newLevel: Int) {
        this.newLevel = Math.max(0, Math.min(newLevel, 8))
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.drop = drop
        this.itemUsed = itemUsed
        this.newLevel = Math.max(0, Math.min(newLevel, 8))
    }
}