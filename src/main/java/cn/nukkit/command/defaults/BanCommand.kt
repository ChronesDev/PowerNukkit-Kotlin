package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BanCommand(name: String?) : VanillaCommand(name, "%nukkit.command.ban.player.description", "%commands.ban.usage") {
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
        sender.getServer().getNameBans().addBan(name, reason.toString(), null, sender.getName())
        val player: Player = sender.getServer().getPlayerExact(name)
        if (player != null) {
            player.kick(PlayerKickEvent.Reason.NAME_BANNED, if (reason.length() > 0) "Banned by admin. Reason: $reason" else "Banned by admin")
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("%commands.ban.success", if (player != null) player.getName() else name))
        return true
    }

    init {
        this.setPermission("nukkit.command.ban.player")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("reason", true, CommandParamType.STRING)
        ))
    }
}