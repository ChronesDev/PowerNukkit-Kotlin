package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BanIpCommand(name: String?) : VanillaCommand(name, "%nukkit.command.ban.ip.description", "%commands.banip.usage") {
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leak")
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        var value = args[0]
        var reason = StringBuilder()
        for (i in 1 until args.size) {
            reason.append(args[i]).append(" ")
        }
        if (reason.length() > 0) {
            reason = StringBuilder(reason.substring(0, reason.length() - 1))
        }
        if (Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", value)) {
            processIPBan(value, sender, reason.toString())
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.banip.success", value))
        } else {
            val player: Player = sender.getServer().getPlayer(value)
            if (player != null) {
                processIPBan(player.getAddress(), sender, reason.toString())
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.banip.success.players", player.getAddress(), player.getName()))
            } else {
                val name: String = value.toLowerCase()
                val path: String = sender.getServer().getDataPath().toString() + "players/"
                val file = File("$path$name.dat")
                var nbt: CompoundTag? = null
                if (file.exists()) {
                    try {
                        FileInputStream(file).use { inputStream -> nbt = NBTIO.readCompressed(inputStream) }
                    } catch (e: IOException) {
                        throw UncheckedIOException(e)
                    }
                }
                if (nbt != null && nbt.contains("lastIP") && Pattern.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$", nbt.getString("lastIP").also { value = it })) {
                    processIPBan(value, sender, reason.toString())
                    Command.broadcastCommandMessage(sender, TranslationContainer("commands.banip.success", value))
                } else {
                    sender.sendMessage(TranslationContainer("commands.banip.invalid"))
                    return false
                }
            }
        }
        return true
    }

    private fun processIPBan(ip: String, sender: CommandSender, reason: String) {
        sender.getServer().getIPBans().addBan(ip, reason, null, sender.getName())
        for (player in ArrayList(sender.getServer().getOnlinePlayers().values())) {
            if (player.getAddress().equals(ip)) {
                player.kick(PlayerKickEvent.Reason.IP_BANNED, if (!reason.isEmpty()) reason else "IP banned")
            }
        }
        try {
            sender.getServer().getNetwork().blockAddress(InetAddress.getByName(ip), -1)
        } catch (e: UnknownHostException) {
            // ignore
        }
    }

    init {
        this.setPermission("nukkit.command.ban.ip")
        this.setAliases(arrayOf("banip"))
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("reason", true, CommandParamType.STRING)
        ))
        this.commandParameters.put("byIp", arrayOf<CommandParameter>(
                CommandParameter.newType("ip", CommandParamType.STRING),
                CommandParameter.newType("reason", true, CommandParamType.STRING)
        ))
    }
}