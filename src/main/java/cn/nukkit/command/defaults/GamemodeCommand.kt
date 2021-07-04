package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/13
 */
class GamemodeCommand(name: String?) : VanillaCommand(name, "%nukkit.command.gamemode.description", "%commands.gamemode.usage", arrayOf("gm")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val gameMode: Int = Server.getGamemodeFromString(args[0])
        if (gameMode == -1) {
            sender.sendMessage("Unknown game mode")
            return true
        }
        var target: CommandSender = sender
        if (args.size > 1) {
            if (sender.hasPermission("nukkit.command.gamemode.other")) {
                target = sender.getServer().getPlayer(args[1])
                if (target == null) {
                    sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
                    return true
                }
            } else {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
        } else if (sender !is Player) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        if (gameMode == 0 && !sender.hasPermission("nukkit.command.gamemode.survival") ||
                gameMode == 1 && !sender.hasPermission("nukkit.command.gamemode.creative") ||
                gameMode == 2 && !sender.hasPermission("nukkit.command.gamemode.adventure") ||
                gameMode == 3 && !sender.hasPermission("nukkit.command.gamemode.spectator")) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
            return true
        }
        if (!(target as Player).setGamemode(gameMode)) {
            sender.sendMessage("Game mode update for " + target.getName().toString() + " failed")
        } else {
            if (target.equals(sender)) {
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.gamemode.success.self", Server.getGamemodeString(gameMode)))
            } else {
                target.sendMessage(TranslationContainer("gameMode.changed", Server.getGamemodeString(gameMode)))
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.gamemode.success.other", target.getName(), Server.getGamemodeString(gameMode)))
            }
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.gamemode.survival;" +
                "nukkit.command.gamemode.creative;" +
                "nukkit.command.gamemode.adventure;" +
                "nukkit.command.gamemode.spectator;" +
                "nukkit.command.gamemode.other")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("gameMode", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        ))
        this.commandParameters.put("byString", arrayOf<CommandParameter>(
                CommandParameter.newEnum("gameMode", CommandEnum.ENUM_GAMEMODE),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        ))
    }
}