package cn.nukkit.event.server

import cn.nukkit.command.CommandSender

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ServerCommandEvent(sender: CommandSender, command: String) : ServerEvent(), Cancellable {
    var command: String
    protected val sender: CommandSender
    fun getSender(): CommandSender {
        return sender
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.sender = sender
        this.command = command
    }
}