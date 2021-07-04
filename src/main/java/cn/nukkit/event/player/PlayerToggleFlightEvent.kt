package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerToggleFlightEvent(player: Player?, isFlying: Boolean) : PlayerEvent(), Cancellable {
    val isFlying: Boolean

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.isFlying = isFlying
    }
}