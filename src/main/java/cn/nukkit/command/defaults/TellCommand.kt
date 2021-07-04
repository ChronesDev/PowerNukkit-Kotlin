package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/12
 */
class TellCommand(name: String?) : VanillaCommand(name, "%nukkit.command.tell.description", "%commands.message.usage", arrayOf("w", "msg")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        val name: String = args[0].toLowerCase()
        val player: Player = sender.getServer().getPlayer(name)
        if (player == null) {
            sender.sendMessage(TranslationContainer("commands.generic.player.notFound"))
            return true
        }
        if (Objects.equals(player, sender)) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.message.sameTarget"))
            return true
        }
        var msg = StringBuilder()
        for (i in 1 until args.size) {
            msg.append(args[i]).append(" ")
        }
        if (msg.length() > 0) {
            msg = StringBuilder(msg.substring(0, msg.length() - 1))
        }
        val displayName: String = if (sender is Player) (sender as Player).getDisplayName() else sender.getName()
        sender.sendMessage("[" + sender.getName().toString() + " -> " + player.getDisplayName().toString() + "] " + msg)
        player.sendMessage("[" + displayName + " -> " + player.getName() + "] " + msg)
        return true
    }

    init {
        this.setPermission("nukkit.command.tell")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("message", CommandParamType.MESSAGE)
        ))
    }
}