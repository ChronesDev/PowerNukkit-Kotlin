package cn.nukkit.event.potion

import cn.nukkit.entity.Entity

/**
 * @author Snake1999
 * @since 2016/1/12
 */
class PotionApplyEvent(potion: Potion, applyEffect: Effect, entity: Entity) : PotionEvent(potion), Cancellable {
    private var applyEffect: Effect
    private val entity: Entity
    fun getEntity(): Entity {
        return entity
    }

    fun getApplyEffect(): Effect {
        return applyEffect
    }

    fun setApplyEffect(applyEffect: Effect) {
        this.applyEffect = applyEffect
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.applyEffect = applyEffect
        this.entity = entity
    }
}