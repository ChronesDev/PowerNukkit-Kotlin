package cn.nukkit.event.level

import cn.nukkit.event.HandlerList

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LevelSaveEvent(level: Level) : LevelEvent(level) {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}