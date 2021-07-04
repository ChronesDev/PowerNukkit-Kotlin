package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class PlayerToggleSwimEvent(player: Player?, isSwimming: Boolean) : PlayerEvent(), Cancellable {
    val isSwimming: Boolean

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.isSwimming = isSwimming
    }
}