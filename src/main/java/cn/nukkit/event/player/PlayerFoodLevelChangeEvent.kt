package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerFoodLevelChangeEvent(player: Player?, foodLevel: Int, foodSaturationLevel: Float) : PlayerEvent(), Cancellable {
    var foodLevel: Int
    var foodSaturationLevel: Float

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.foodLevel = foodLevel
        this.foodSaturationLevel = foodSaturationLevel
    }
}