package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerJoinEvent : PlayerEvent {
    protected var joinMessage: TextContainer

    constructor(player: Player?, joinMessage: TextContainer) {
        player = player
        this.joinMessage = joinMessage
    }

    constructor(player: Player?, joinMessage: String?) {
        player = player
        this.joinMessage = TextContainer(joinMessage)
    }

    fun getJoinMessage(): TextContainer {
        return joinMessage
    }

    fun setJoinMessage(joinMessage: TextContainer) {
        this.joinMessage = joinMessage
    }

    fun setJoinMessage(joinMessage: String?) {
        this.setJoinMessage(TextContainer(joinMessage))
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }
}