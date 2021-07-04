package cn.nukkit.command.defaults

import cn.nukkit.command.CommandSender

/**
 * @author xtypr
 * @since 2015/11/11
 */
class BanListCommand(name: String?) : VanillaCommand(name, "%nukkit.command.banlist.description", "%commands.banlist.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        val list: BanList
        var ips = false
        if (args.size > 0) {
            when (args[0].toLowerCase()) {
                "ips" -> {
                    list = sender.getServer().getIPBans()
                    ips = true
                }
                "players" -> list = sender.getServer().getNameBans()
                else -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return false
                }
            }
        } else {
            list = sender.getServer().getNameBans()
        }
        val builder = StringBuilder()
        val itr: Iterator<BanEntry> = list.getEntires().values().iterator()
        while (itr.hasNext()) {
            builder.append(itr.next().getName())
            if (itr.hasNext()) {
                builder.append(", ")
            }
        }
        if (ips) {
            sender.sendMessage(TranslationContainer("commands.banlist.ips", String.valueOf(list.getEntires().size())))
        } else {
            sender.sendMessage(TranslationContainer("commands.banlist.players", String.valueOf(list.getEntires().size())))
        }
        sender.sendMessage(builder.toString())
        return true
    }

    init {
        this.setPermission("nukkit.command.ban.list")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newEnum("type", true, CommandEnum("BanListType", "ips", "players"))
        ))
    }
}