package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerQuitEvent(player: Player?, quitMessage: TextContainer, autoSave: Boolean, reason: String) : PlayerEvent() {
    protected var quitMessage: TextContainer
    var autoSave = true
    var reason: String
        protected set

    constructor(player: Player?, quitMessage: TextContainer?, reason: String?) : this(player, quitMessage, true, reason) {}
    constructor(player: Player?, quitMessage: TextContainer?) : this(player, quitMessage, true) {}
    constructor(player: Player?, quitMessage: String?, reason: String?) : this(player, quitMessage, true, reason) {}
    constructor(player: Player?, quitMessage: String?) : this(player, quitMessage, true) {}
    constructor(player: Player?, quitMessage: String?, autoSave: Boolean, reason: String?) : this(player, TextContainer(quitMessage), autoSave, reason) {}
    constructor(player: Player?, quitMessage: String?, autoSave: Boolean) : this(player, TextContainer(quitMessage), autoSave) {}
    constructor(player: Player?, quitMessage: TextContainer?, autoSave: Boolean) : this(player, quitMessage, autoSave, "No reason") {}

    fun getQuitMessage(): TextContainer {
        return quitMessage
    }

    fun setQuitMessage(quitMessage: TextContainer) {
        this.quitMessage = quitMessage
    }

    fun setQuitMessage(quitMessage: String?) {
        this.setQuitMessage(TextContainer(quitMessage))
    }

    fun setAutoSave() {
        autoSave = true
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.quitMessage = quitMessage
        this.autoSave = autoSave
        this.reason = reason
    }
}