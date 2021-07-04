package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityLevelChangeEvent(entity: Entity?, originLevel: Level, targetLevel: Level) : EntityEvent(), Cancellable {
    private val originLevel: Level
    private val targetLevel: Level
    val origin: Level
        get() = originLevel
    val target: Level
        get() = targetLevel

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.originLevel = originLevel
        this.targetLevel = targetLevel
    }
}