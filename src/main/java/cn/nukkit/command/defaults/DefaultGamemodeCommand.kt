package cn.nukkit.command.defaults

import cn.nukkit.Server

/**
 * @author xtypr
 * @since 2015/11/12
 */
class DefaultGamemodeCommand(name: String?) : VanillaCommand(name, "%nukkit.command.defaultgamemode.description", "%commands.defaultgamemode.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", arrayOf<String>(this.usageMessage)))
            return false
        }
        val gameMode: Int = Server.getGamemodeFromString(args[0])
        if (gameMode != -1) {
            sender.getServer().setPropertyInt("gamemode", gameMode)
            sender.sendMessage(TranslationContainer("commands.defaultgamemode.success", arrayOf<String>(Server.getGamemodeString(gameMode))))
        } else {
            sender.sendMessage("Unknown game mode") //
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.defaultgamemode")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("gameMode", CommandParamType.INT)
        ))
        this.commandParameters.put("byString", arrayOf<CommandParameter>(
                CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE)
        ))
    }
}