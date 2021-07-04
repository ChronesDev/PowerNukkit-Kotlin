package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerJumpEvent(player: Player?) : PlayerEvent() {
    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
    }
}