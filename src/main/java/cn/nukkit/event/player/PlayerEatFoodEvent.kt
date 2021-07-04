package cn.nukkit.event.player

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class PlayerEatFoodEvent(player: Player?, food: Food) : PlayerEvent(), Cancellable {
    private var food: Food
    fun getFood(): Food {
        return food
    }

    fun setFood(food: Food) {
        this.food = food
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.food = food
    }
}