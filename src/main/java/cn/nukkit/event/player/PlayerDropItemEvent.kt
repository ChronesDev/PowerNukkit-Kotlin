package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerDropItemEvent(player: Player?, drop: Item) : PlayerEvent(), Cancellable {
    private val drop: Item
    val item: Item
        get() = drop

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.drop = drop
    }
}