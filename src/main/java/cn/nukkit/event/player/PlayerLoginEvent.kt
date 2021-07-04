package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerLoginEvent(player: Player?, kickMessage: String) : PlayerEvent(), Cancellable {
    var kickMessage: String

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.kickMessage = kickMessage
    }
}