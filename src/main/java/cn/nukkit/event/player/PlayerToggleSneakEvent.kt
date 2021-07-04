package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerToggleSneakEvent(player: Player?, isSneaking: Boolean) : PlayerEvent(), Cancellable {
    val isSneaking: Boolean

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.isSneaking = isSneaking
    }
}