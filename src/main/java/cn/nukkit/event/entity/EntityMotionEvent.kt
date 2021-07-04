package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityMotionEvent(entity: Entity?, motion: Vector3) : EntityEvent(), Cancellable {
    private val motion: Vector3

    @get:Deprecated
    val vector: Vector3
        get() = motion

    fun getMotion(): Vector3 {
        return motion
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.motion = motion
    }
}