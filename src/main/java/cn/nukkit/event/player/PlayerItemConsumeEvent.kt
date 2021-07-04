package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * Called when a player eats something
 */
class PlayerItemConsumeEvent(player: Player?, item: Item) : PlayerEvent(), Cancellable {
    private val item: Item
    fun getItem(): Item {
        return item.clone()
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.item = item
    }
}