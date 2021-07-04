package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/11
 */
class KickCommand(name: String?) : VanillaCommand(name, "%nukkit.command.kick.description", "%commands.kick.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val name = args[0]
        var reason = StringBuilder()
        for (i in 1 until args.size) {
            reason.append(args[i]).append(" ")
        }
        if (reason.length() > 0) {
            reason = StringBuilder(reason.substring(0, reason.length() - 1))
        }
        val player: Player = sender.getServer().getPlayer(name)
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.KICKED_BY_ADMIN, reason.toString())
            if (reason.length() >= 1) {
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.kick.success.reason", player.getName(), reason.toString())
                )
            } else {
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.kick.success", player.getName()))
            }
        } else {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.kick")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("reason", true, CommandParamType.MESSAGE)
        ))
    }
}