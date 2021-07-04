package cn.nukkit.event.potion

import cn.nukkit.entity.item.EntityPotion

/**
 * @author Snake1999
 * @since 2016/1/12
 */
class PotionCollideEvent(potion: Potion, thrownPotion: EntityPotion) : PotionEvent(potion), Cancellable {
    private val thrownPotion: EntityPotion
    fun getThrownPotion(): EntityPotion {
        return thrownPotion
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.thrownPotion = thrownPotion
    }
}