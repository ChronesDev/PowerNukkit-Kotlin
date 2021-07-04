package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityRegainHealthEvent(entity: Entity?, amount: Float, regainReason: Int) : EntityEvent(), Cancellable {
    var amount: Float
    val regainReason: Int

    companion object {
        val handlers: HandlerList = HandlerList()
        const val CAUSE_REGEN = 0
        const val CAUSE_EATING = 1
        const val CAUSE_MAGIC = 2
        const val CAUSE_CUSTOM = 3
    }

    init {
        entity = entity
        this.amount = amount
        this.regainReason = regainReason
    }
}