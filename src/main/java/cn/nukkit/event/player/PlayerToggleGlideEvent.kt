package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerToggleGlideEvent(player: Player?, isSneaking: Boolean) : PlayerEvent(), Cancellable {
    val isGliding: Boolean

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        isGliding = isSneaking
    }
}