package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class PlayerEvent : Event() {
    protected var player: Player? = null
    fun getPlayer(): Player? {
        return player
    }
}