package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerToggleSprintEvent(player: Player?, isSprinting: Boolean) : PlayerEvent(), Cancellable {
    val isSprinting: Boolean

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.isSprinting = isSprinting
    }
}