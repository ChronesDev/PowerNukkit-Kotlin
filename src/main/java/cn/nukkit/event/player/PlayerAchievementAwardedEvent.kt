package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerAchievementAwardedEvent(player: Player?, achievementId: String) : PlayerEvent(), Cancellable {
    val achievement: String

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        achievement = achievementId
    }
}