package cn.nukkit.command.defaults

import cn.nukkit.Nukkit

/**
 * @author xtypr
 * @since 2015/11/11
 */
class StatusCommand(name: String?) : VanillaCommand(name, "%nukkit.command.status.description", "%nukkit.command.status.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        val server: Server = sender.getServer()
        sender.sendMessage(TextFormat.GREEN.toString() + "---- " + TextFormat.WHITE + "Server status" + TextFormat.GREEN + " ----")
        val time: Long = System.currentTimeMillis() - Nukkit.START_TIME
        sender.sendMessage(TextFormat.GOLD.toString() + "Uptime: " + formatUptime(time))
        var tpsColor: TextFormat = TextFormat.GREEN
        val tps: Float = server.getTicksPerSecond()
        if (tps < 17) {
            tpsColor = TextFormat.GOLD
        } else if (tps < 12) {
            tpsColor = TextFormat.RED
        }
        sender.sendMessage(TextFormat.GOLD.toString() + "Current TPS: " + tpsColor + NukkitMath.round(tps, 2))
        sender.sendMessage(TextFormat.GOLD.toString() + "Load: " + tpsColor + server.getTickUsage() + "%")
        sender.sendMessage(TextFormat.GOLD.toString() + "Network upload: " + TextFormat.GREEN + NukkitMath.round(server.getNetwork().getUpload() / 1024 * 1000, 2) + " kB/s")
        sender.sendMessage(TextFormat.GOLD.toString() + "Network download: " + TextFormat.GREEN + NukkitMath.round(server.getNetwork().getDownload() / 1024 * 1000, 2) + " kB/s")
        sender.sendMessage(TextFormat.GOLD.toString() + "Thread count: " + TextFormat.GREEN + Thread.getAllStackTraces().size())
        val runtime: Runtime = Runtime.getRuntime()
        val totalMB: Double = NukkitMath.round(runtime.totalMemory() as Double / 1024 / 1024, 2)
        val usedMB: Double = NukkitMath.round((runtime.totalMemory() - runtime.freeMemory()) as Double / 1024 / 1024, 2)
        val maxMB: Double = NukkitMath.round(runtime.maxMemory() as Double / 1024 / 1024, 2)
        val usage = usedMB / maxMB * 100
        var usageColor: TextFormat = TextFormat.GREEN
        if (usage > 85) {
            usageColor = TextFormat.GOLD
        }
        sender.sendMessage(TextFormat.GOLD.toString() + "Used memory: " + usageColor + usedMB + " MB. (" + NukkitMath.round(usage, 2) + "%)")
        sender.sendMessage(TextFormat.GOLD.toString() + "Total memory: " + TextFormat.RED + totalMB + " MB.")
        sender.sendMessage(TextFormat.GOLD.toString() + "Maximum VM memory: " + TextFormat.RED + maxMB + " MB.")
        sender.sendMessage(TextFormat.GOLD.toString() + "Available processors: " + TextFormat.GREEN + runtime.availableProcessors())
        var playerColor: TextFormat = TextFormat.GREEN
        if (server.getOnlinePlayers().size() as Float / server.getMaxPlayers() as Float > 0.85) {
            playerColor = TextFormat.GOLD
        }
        sender.sendMessage(TextFormat.GOLD.toString() + "Players: " + playerColor + server.getOnlinePlayers().size() + TextFormat.GREEN + " online, " +
                TextFormat.RED + server.getMaxPlayers() + TextFormat.GREEN + " max. ")
        for (level in server.getLevels().values()) {
            sender.sendMessage(
                    TextFormat.GOLD.toString() + "World \"" + level.getFolderName() + "\"" + (if (!Objects.equals(level.getFolderName(), level.getName())) " (" + level.getName().toString() + ")" else "") + ": " +
                            TextFormat.RED + level.getChunks().size() + TextFormat.GREEN + " chunks, " +
                            TextFormat.RED + level.getEntities().length + TextFormat.GREEN + " entities, " +
                            TextFormat.RED + level.getBlockEntities().size() + TextFormat.GREEN + " blockEntities." +
                            " Time " + (if (level.getTickRate() > 1 || level.getTickRateTime() > 40) TextFormat.RED else TextFormat.YELLOW) + NukkitMath.round(level.getTickRateTime(), 2) + "ms" +
                            if (level.getTickRate() > 1) " (tick rate " + (19 - level.getTickRate()).toString() + ")" else ""
            )
        }
        return true
    }

    companion object {
        private val UPTIME_FORMAT: String = TextFormat.RED.toString() + "%d" + TextFormat.GOLD + " days " +
                TextFormat.RED + "%d" + TextFormat.GOLD + " hours " +
                TextFormat.RED + "%d" + TextFormat.GOLD + " minutes " +
                TextFormat.RED + "%d" + TextFormat.GOLD + " seconds"

        private fun formatUptime(uptime: Long): String {
            var uptime = uptime
            val days: Long = TimeUnit.MILLISECONDS.toDays(uptime)
            uptime -= TimeUnit.DAYS.toMillis(days)
            val hours: Long = TimeUnit.MILLISECONDS.toHours(uptime)
            uptime -= TimeUnit.HOURS.toMillis(hours)
            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(uptime)
            uptime -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(uptime)
            return String.format(UPTIME_FORMAT, days, hours, minutes, seconds)
        }
    }

    init {
        this.setPermission("nukkit.command.status")
        this.commandParameters.clear()
    }
}