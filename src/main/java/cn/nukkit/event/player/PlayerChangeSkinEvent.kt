package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author KCodeYT (Nukkit Project)
 */
class PlayerChangeSkinEvent(player: Player?, skin: Skin) : PlayerEvent(), Cancellable {
    private val skin: Skin
    fun getSkin(): Skin {
        return skin
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.skin = skin
    }
}