package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PardonIpCommand(name: String?) : VanillaCommand(name, "%nukkit.command.unban.ip.description", "%commands.unbanip.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size != 1) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val value = args[0]
        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            sender.getServer().getIPBans().remove(value)
            try {
                sender.getServer().getNetwork().unblockAddress(InetAddress.getByName(value))
            } catch (e: UnknownHostException) {
                sender.sendMessage(TranslationContainer("commands.unbanip.invalid"))
                return true
            }
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.unbanip.success", value))
        } else {
            sender.sendMessage(TranslationContainer("commands.unbanip.invalid"))
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.unban.ip")
        this.setAliases(arrayOf("unbanip", "unban-ip", "pardonip"))
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("ip", CommandParamType.STRING)
        ))
    }
}