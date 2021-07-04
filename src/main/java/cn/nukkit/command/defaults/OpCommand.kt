package cn.nukkit.command.defaults

import cn.nukkit.IPlayer

/**
 * @author xtypr
 * @since 2015/11/12
 */
class OpCommand(name: String?) : VanillaCommand(name, "%nukkit.command.op.description", "%commands.op.description") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.op.usage", this.usageMessage))
            return false
        }
        val name = args[0]
        val player: IPlayer = sender.getServer().getOfflinePlayer(name)
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.op.success", player.getName()))
        if (player is Player) {
            (player as Player).sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%commands.op.message"))
        }
        player.setOp(true)
        return true
    }

    init {
        this.setPermission("nukkit.command.op.give")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET)
        ))
    }
}