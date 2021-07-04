package cn.nukkit.event.level

import cn.nukkit.event.HandlerList

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LevelInitEvent(level: Level) : LevelEvent(level) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}