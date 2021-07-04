package cn.nukkit.event.potion

import cn.nukkit.event.Event

/**
 * @author Snake1999
 * @since 2016/1/12
 */
abstract class PotionEvent(potion: Potion) : Event() {
    private var potion: Potion
    fun getPotion(): Potion {
        return potion
    }

    fun setPotion(potion: Potion) {
        this.potion = potion
    }

    init {
        this.potion = potion
    }
}