package cn.nukkit.command.defaults

import cn.nukkit.IPlayer

/**
 * @author xtypr
 * @since 2015/11/12
 */
class DeopCommand(name: String?) : VanillaCommand(name, "%nukkit.command.deop.description", "%commands.deop.description") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val playerName = args[0]
        val player: IPlayer = sender.getServer().getOfflinePlayer(playerName)
        player.setOp(false)
        if (player is Player) {
            (player as Player).sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%commands.deop.message"))
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.deop.success", arrayOf<String>(player.getName())))
        return true
    }

    init {
        this.setPermission("nukkit.command.op.take")
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET)
        ))
    }
}