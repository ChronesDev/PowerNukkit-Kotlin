package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerCommandPreprocessEvent(player: Player?, message: String?) : PlayerMessageEvent(), Cancellable {
    override var player: Player?
        get() = super.player

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        message = message
    }
}