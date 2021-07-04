package cn.nukkit.event.level

import cn.nukkit.event.Event

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class LevelEvent(level: Level) : Event() {
    private val level: Level
    fun getLevel(): Level {
        return level
    }

    init {
        this.level = level
    }
}