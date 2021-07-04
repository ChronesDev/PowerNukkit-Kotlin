package cn.nukkit.event.level

import cn.nukkit.event.HandlerList

/**
 * @author MagicDroidX (Nukkit Project)
 */
class SpawnChangeEvent(level: Level, previousSpawn: Position) : LevelEvent(level) {
    private val previousSpawn: Position
    fun getPreviousSpawn(): Position {
        return previousSpawn
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.previousSpawn = previousSpawn
    }
}