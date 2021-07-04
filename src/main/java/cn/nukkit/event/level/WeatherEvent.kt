package cn.nukkit.event.level

import cn.nukkit.event.Event

/**
 * @author funcraft (Nukkit Project)
 */
abstract class WeatherEvent(level: Level) : Event() {
    private val level: Level
    fun getLevel(): Level {
        return level
    }

    init {
        this.level = level
    }
}