package cn.nukkit.command

import cn.nukkit.lang.TextContainer

/**
 * Represents an RCON command sender.
 *
 * @author Tee7even
 */
class RemoteConsoleCommandSender : ConsoleCommandSender() {
    private val messages: StringBuilder = StringBuilder()

    @Override
    override fun sendMessage(message: String) {
        var message = message
        message = this.getServer().getLanguage().translateString(message)
        messages.append(message.trim()).append("\n")
    }

    @Override
    override fun sendMessage(message: TextContainer?) {
        this.sendMessage(this.getServer().getLanguage().translate(message))
    }

    fun getMessages(): String {
        return messages.toString()
    }

    @get:Override
    override val name: String
        get() = "Rcon"
}