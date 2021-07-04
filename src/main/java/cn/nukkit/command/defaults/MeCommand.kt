package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/12
 */
class MeCommand(name: String?) : VanillaCommand(name, "%nukkit.command.me.description", "%commands.me.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val name: String
        name = if (sender is Player) {
            (sender as Player).getDisplayName()
        } else {
            sender.getName()
        }
        var msg = StringBuilder()
        for (arg in args) {
            msg.append(arg).append(" ")
        }
        if (msg.length() > 0) {
            msg = StringBuilder(msg.substring(0, msg.length() - 1))
        }
        sender.getServer().broadcastMessage(TranslationContainer("chat.type.emote", name, TextFormat.WHITE + msg.toString()))
        return true
    }

    init {
        this.setPermission("nukkit.command.me")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        ))
    }
}