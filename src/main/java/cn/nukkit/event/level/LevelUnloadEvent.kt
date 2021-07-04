package cn.nukkit.event.level

import cn.nukkit.event.Cancellable

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LevelUnloadEvent(level: Level) : LevelEvent(level), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}