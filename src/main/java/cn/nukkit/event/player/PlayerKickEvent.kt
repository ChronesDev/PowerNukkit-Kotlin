package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerKickEvent(player: Player?, reason: Reason, reasonString: String?, quitMessage: TextContainer?) : PlayerEvent(), Cancellable {
    enum class Reason {
        NEW_CONNECTION, KICKED_BY_ADMIN, NOT_WHITELISTED, IP_BANNED, NAME_BANNED, INVALID_PVE, LOGIN_TIMEOUT, SERVER_FULL, FLYING_DISABLED, INVALID_PVP, UNKNOWN;

        @Override
        override fun toString(): String {
            return this.name()
        }
    }

    protected var quitMessage: TextContainer?
    var reasonEnum: Reason
    val reason: String

    @Deprecated
    constructor(player: Player?, reason: String?, quitMessage: String?) : this(player, Reason.UNKNOWN, reason, TextContainer(quitMessage)) {
    }

    @Deprecated
    constructor(player: Player?, reason: String?, quitMessage: TextContainer?) : this(player, Reason.UNKNOWN, reason, quitMessage) {
    }

    constructor(player: Player?, reason: Reason, quitMessage: TextContainer?) : this(player, reason, reason.toString(), quitMessage) {}
    constructor(player: Player?, reason: Reason?, quitMessage: String?) : this(player, reason, TextContainer(quitMessage)) {}

    fun getQuitMessage(): TextContainer? {
        return quitMessage
    }

    fun setQuitMessage(quitMessage: TextContainer?) {
        this.quitMessage = quitMessage
    }

    fun setQuitMessage(joinMessage: String?) {
        this.setQuitMessage(TextContainer(joinMessage))
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.quitMessage = quitMessage
        reasonEnum = reason
        reasonEnum = reason.name()
    }
}