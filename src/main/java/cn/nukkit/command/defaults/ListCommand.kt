package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/11
 */
class ListCommand(name: String?) : VanillaCommand(name, "%nukkit.command.list.description", "%commands.players.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        var online = StringBuilder()
        var onlineCount = 0
        for (player in sender.getServer().getOnlinePlayers().values()) {
            if (player.isOnline() && (sender !is Player || (sender as Player).canSee(player))) {
                online.append(player.getDisplayName()).append(", ")
                ++onlineCount
            }
        }
        if (online.length() > 0) {
            online = StringBuilder(online.substring(0, online.length() - 2))
        }
        sender.sendMessage(TranslationContainer("commands.players.list",
                String.valueOf(onlineCount), String.valueOf(sender.getServer().getMaxPlayers())))
        sender.sendMessage(online.toString())
        return true
    }

    init {
        this.setPermission("nukkit.command.list")
        this.commandParameters.clear()
    }
}