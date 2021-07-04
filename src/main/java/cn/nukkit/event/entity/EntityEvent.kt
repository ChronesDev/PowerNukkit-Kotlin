package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityEvent : Event() {
    protected var entity: Entity? = null
    fun getEntity(): Entity? {
        return entity
    }
}