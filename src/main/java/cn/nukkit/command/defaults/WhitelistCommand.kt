package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author xtypr
 * @since 2015/11/12
 */
class WhitelistCommand(name: String?) : VanillaCommand(name, "%nukkit.command.whitelist.description", "%commands.whitelist.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0 || args.size > 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        if (args.size == 1) {
            if (badPerm(sender, args[0].toLowerCase())) {
                return false
            }
            when (args[0].toLowerCase()) {
                "reload" -> {
                    sender.getServer().reloadWhitelist()
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.whitelist.reloaded"))
                    return true
                }
                "on" -> {
                    sender.getServer().setPropertyBoolean("white-list", true)
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.whitelist.enabled"))
                    return true
                }
                "off" -> {
                    sender.getServer().setPropertyBoolean("white-list", false)
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.whitelist.disabled"))
                    return true
                }
                "list" -> {
                    val result = StringBuilder()
                    var count = 0
                    for (player in sender.getServer().getWhitelist().getAll().keySet()) {
                        result.append(player).append(", ")
                        ++count
                    }
                    sender.sendMessage(TranslationContainer("commands.whitelist.list", String.valueOf(count), String.valueOf(count)))
                    sender.sendMessage(if (result.length() > 0) result.substring(0, result.length() - 2) else "")
                    return true
                }
                "add" -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", "%commands.whitelist.add.usage"))
                    return true
                }
                "remove" -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", "%commands.whitelist.remove.usage"))
                    return true
                }
            }
        } else if (args.size == 2) {
            if (badPerm(sender, args[0].toLowerCase())) {
                return false
            }
            when (args[0].toLowerCase()) {
                "add" -> {
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(true)
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.whitelist.add.success", args[1]))
                    return true
                }
                "remove" -> {
                    sender.getServer().getOfflinePlayer(args[1]).setWhitelisted(false)
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.whitelist.remove.success", args[1]))
                    return true
                }
            }
        }
        return true
    }

    private fun badPerm(sender: CommandSender, perm: String): Boolean {
        if (!sender.hasPermission("nukkit.command.whitelist.$perm")) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
            return true
        }
        return false
    }

    init {
        this.setPermission(
                "nukkit.command.whitelist.reload;" +
                        "nukkit.command.whitelist.enable;" +
                        "nukkit.command.whitelist.disable;" +
                        "nukkit.command.whitelist.list;" +
                        "nukkit.command.whitelist.add;" +
                        "nukkit.command.whitelist.remove"
        )
        this.commandParameters.clear()
        this.commandParameters.put("1arg", arrayOf<CommandParameter>(
                CommandParameter.newEnum("action", CommandEnum("WhitelistAction", "on", "off", "list", "reload"))
        ))
        this.commandParameters.put("2args", arrayOf<CommandParameter>(
                CommandParameter.newEnum("action", CommandEnum("WhitelistPlayerAction", "add", "remove")),
                CommandParameter.newType("player", CommandParamType.TARGET)
        ))
    }
}