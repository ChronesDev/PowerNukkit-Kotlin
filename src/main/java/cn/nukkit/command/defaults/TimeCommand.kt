package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/11
 */
class TimeCommand(name: String?) : VanillaCommand(name, "%nukkit.command.time.description", "%nukkit.command.time.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (args.size < 1) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        if ("start".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.start")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            for (level in sender.getServer().getLevels().values()) {
                level.checkTime()
                level.startTime()
                level.checkTime()
            }
            Command.broadcastCommandMessage(sender, "Restarted the time")
            return true
        } else if ("stop".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.stop")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            for (level in sender.getServer().getLevels().values()) {
                level.checkTime()
                level.stopTime()
                level.checkTime()
            }
            Command.broadcastCommandMessage(sender, "Stopped the time")
            return true
        } else if ("query".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.query")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            val level: Level
            level = if (sender is Player) {
                (sender as Player).getLevel()
            } else {
                sender.getServer().getDefaultLevel()
            }
            sender.sendMessage(TranslationContainer("commands.time.query.gametime", String.valueOf(level.getTime())))
            return true
        }
        if (args.size < 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        if ("set".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.set")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            val value: Int
            value = if ("day".equals(args[1])) {
                Level.TIME_DAY
            } else if ("night".equals(args[1])) {
                Level.TIME_NIGHT
            } else if ("midnight".equals(args[1])) {
                Level.TIME_MIDNIGHT
            } else if ("noon".equals(args[1])) {
                Level.TIME_NOON
            } else if ("sunrise".equals(args[1])) {
                Level.TIME_SUNRISE
            } else if ("sunset".equals(args[1])) {
                Level.TIME_SUNSET
            } else {
                try {
                    Math.max(0, Integer.parseInt(args[1]))
                } catch (e: Exception) {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return true
                }
            }
            for (level in sender.getServer().getLevels().values()) {
                level.checkTime()
                level.setTime(value)
                level.checkTime()
            }
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.time.set", String.valueOf(value)))
        } else if ("add".equals(args[0])) {
            if (!sender.hasPermission("nukkit.command.time.add")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            val value: Int
            value = try {
                Math.max(0, Integer.parseInt(args[1]))
            } catch (e: Exception) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
            for (level in sender.getServer().getLevels().values()) {
                level.checkTime()
                level.setTime(level.getTime() + value)
                level.checkTime()
            }
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.time.added", String.valueOf(value)))
        } else {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.time.add;" +
                "nukkit.command.time.set;" +
                "nukkit.command.time.start;" +
                "nukkit.command.time.stop")
        this.commandParameters.clear()
        this.commandParameters.put("1arg", arrayOf<CommandParameter>(
                CommandParameter.newEnum("mode", CommandEnum("TimeMode", "query", "start", "stop"))
        ))
        this.commandParameters.put("add", arrayOf<CommandParameter>(
                CommandParameter.newEnum("mode", CommandEnum("TimeModeAdd", "add")),
                CommandParameter.newType("amount", CommandParamType.INT)
        ))
        this.commandParameters.put("setAmount", arrayOf<CommandParameter>(
                CommandParameter.newEnum("mode", false, CommandEnum("TimeModeSet", "set")),
                CommandParameter.newType("amount", CommandParamType.INT)
        ))
        this.commandParameters.put("setTime", arrayOf<CommandParameter>(
                CommandParameter.newEnum("mode", CommandEnum("TimeModeSet", "set")),
                CommandParameter.newEnum("time", CommandEnum("TimeSpec", "day", "night", "midnight", "noon", "sunrise", "sunset"))
        ))
    }
}