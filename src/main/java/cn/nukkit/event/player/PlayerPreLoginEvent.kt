package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * Called when the player logs in, before things have been set up
 */
class PlayerPreLoginEvent(player: Player?, kickMessage: String) : PlayerEvent(), Cancellable {
    var kickMessage: String

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.kickMessage = kickMessage
    }
}