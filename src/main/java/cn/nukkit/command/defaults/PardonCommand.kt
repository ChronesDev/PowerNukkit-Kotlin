package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PardonCommand(name: String?) : VanillaCommand(name, "%nukkit.command.unban.player.description", "%commands.unban.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size != 1) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        sender.getServer().getNameBans().remove(args[0])
        Command.broadcastCommandMessage(sender, TranslationContainer("%commands.unban.success", args[0]))
        return true
    }

    init {
        this.setPermission("nukkit.command.unban.player")
        this.setAliases(arrayOf("unban"))
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET)
        ))
    }
}