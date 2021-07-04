package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerChatEvent(player: Player?, message: String?, format: String, recipients: Set<CommandSender>?) : PlayerMessageEvent(), Cancellable {
    var format: String
    protected var recipients: Set<CommandSender> = HashSet()

    constructor(player: Player?, message: String?) : this(player, message, "chat.type.text", null) {}

    /**
     * Changes the player that is sending the message
     *
     * @param player messenger
     */
    override var player: Player?
        get() = super.player

    fun getRecipients(): Set<CommandSender> {
        return recipients
    }

    fun setRecipients(recipients: Set<CommandSender>) {
        this.recipients = recipients
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        message = message
        this.format = format
        if (recipients == null) {
            for (permissible in Server.getInstance().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS)) {
                if (permissible is CommandSender) {
                    this.recipients.add(permissible as CommandSender)
                }
            }
        } else {
            this.recipients = recipients
        }
    }
}