package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author Extollite (Nukkit Project)
 */
@Since("1.4.0.0-PN")
class PlayerLocallyInitializedEvent @Since("1.4.0.0-PN") constructor(player: Player?) : PlayerEvent() {
    companion object {
        val handlers: HandlerList = HandlerList()
            @Since("1.4.0.0-PN") get
    }

    init {
        player = player
    }
}