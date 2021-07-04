package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Tee7even
 */
class TitleCommand(name: String?) : VanillaCommand(name, "%nukkit.command.title.description", "%nukkit.command.title.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val player: Player = Server.getInstance().getPlayerExact(args[0])
        if (player == null) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
            return true
        }
        if (args.size == 2) {
            when (args[1].toLowerCase()) {
                "clear" -> {
                    player.clearTitle()
                    sender.sendMessage(TranslationContainer("nukkit.command.title.clear", player.getName()))
                }
                "reset" -> {
                    player.resetTitleSettings()
                    sender.sendMessage(TranslationContainer("nukkit.command.title.reset", player.getName()))
                }
                else -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return false
                }
            }
        } else if (args.size == 3) {
            when (args[1].toLowerCase()) {
                "title" -> {
                    player.sendTitle(args[2])
                    sender.sendMessage(TranslationContainer("nukkit.command.title.title",
                            TextFormat.clean(args[2]), player.getName()))
                }
                "subtitle" -> {
                    player.setSubtitle(args[2])
                    sender.sendMessage(TranslationContainer("nukkit.command.title.subtitle", TextFormat.clean(args[2]), player.getName()))
                }
                else -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return false
                }
            }
        } else if (args.size == 5) {
            if (args[1].toLowerCase().equals("times")) {
                try {
                    /*player.setTitleAnimationTimes(Integer.valueOf(args[2]), //fadeIn
                            Integer.valueOf(args[3]), //stay
                            Integer.valueOf(args[4])); //fadeOut*/
                    sender.sendMessage(TranslationContainer("nukkit.command.title.times.success",
                            args[2], args[3], args[4], player.getName()))
                } catch (exception: NumberFormatException) {
                    sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%nukkit.command.title.times.fail"))
                }
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return false
            }
        } else {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.title")
        this.commandParameters.clear()
        this.commandParameters.put("clear", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", CommandEnum("TitleClear", "clear"))
        ))
        this.commandParameters.put("reset", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("reset", CommandEnum("TitleReset", "reset"))
        ))
        this.commandParameters.put("set", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("titleLocation", CommandEnum("TitleSet", "title", "subtitle", "actionbar")),
                CommandParameter.newType("titleText", CommandParamType.MESSAGE)
        ))
        this.commandParameters.put("times", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("times", CommandEnum("TitleTimes", "times")),
                CommandParameter.newType("fadeIn", CommandParamType.INT),
                CommandParameter.newType("stay", CommandParamType.INT),
                CommandParameter.newType("fadeOut", CommandParamType.INT)
        ))
    }
}