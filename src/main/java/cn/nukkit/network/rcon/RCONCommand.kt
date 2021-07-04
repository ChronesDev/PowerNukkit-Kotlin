package cn.nukkit.network.rcon

import java.nio.channels.SocketChannel

/**
 * A data structure to hold sender, request ID and command itself.
 *
 * @author Tee7even
 */
class RCONCommand(sender: SocketChannel, id: Int, command: String) {
    private val sender: SocketChannel
    val id: Int
    val command: String
    fun getSender(): SocketChannel {
        return sender
    }

    init {
        this.sender = sender
        this.id = id
        this.command = command
    }
}