package cn.nukkit.command.simple

import cn.nukkit.command.Command

/**
 * @author Tee7even
 */
@Log4j2
class SimpleCommand(`object`: Object, method: Method, name: String?, description: String?, usageMessage: String?, aliases: Array<String?>?) : Command(name!!, description, usageMessage, aliases) {
    private val `object`: Object
    private val method: Method
    private var forbidConsole = false
    private var maxArgs = 0
    private var minArgs = 0
    fun setForbidConsole(forbidConsole: Boolean) {
        this.forbidConsole = forbidConsole
    }

    fun setMaxArgs(maxArgs: Int) {
        this.maxArgs = maxArgs
    }

    fun setMinArgs(minArgs: Int) {
        this.minArgs = minArgs
    }

    fun sendUsageMessage(sender: CommandSender) {
        if (!this.usageMessage.equals("")) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
        }
    }

    fun sendInGameMessage(sender: CommandSender) {
        sender.sendMessage(TranslationContainer("commands.generic.ingame"))
    }

    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (forbidConsole && sender is ConsoleCommandSender) {
            sendInGameMessage(sender)
            return false
        } else if (!this.testPermission(sender)) {
            return false
        } else if (maxArgs != 0 && args.size > maxArgs) {
            sendUsageMessage(sender)
            return false
        } else if (minArgs != 0 && args.size < minArgs) {
            sendUsageMessage(sender)
            return false
        }
        var success = false
        try {
            success = method.invoke(`object`, sender, commandLabel, args)
        } catch (exception: Exception) {
            log.error("Failed to execute {} by {}", commandLabel, sender.getName(), exception)
        }
        if (!success) {
            sendUsageMessage(sender)
        }
        return success
    }

    init {
        this.`object` = `object`
        this.method = method
    }
}