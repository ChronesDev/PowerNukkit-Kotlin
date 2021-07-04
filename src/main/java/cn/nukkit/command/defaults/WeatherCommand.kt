package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class WeatherCommand(name: String?) : VanillaCommand(name, "%nukkit.command.weather.description", "%commands.weather.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size == 0 || args.size > 2) {
            sender.sendMessage(TranslationContainer("commands.weather.usage", this.usageMessage))
            return false
        }
        val weather = args[0]
        val level: Level
        val seconds: Int
        seconds = if (args.size > 1) {
            try {
                Integer.parseInt(args[1])
            } catch (e: Exception) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
        } else {
            600 * 20
        }
        level = if (sender is Player) {
            (sender as Player).getLevel()
        } else {
            sender.getServer().getDefaultLevel()
        }
        return when (weather) {
            "clear" -> {
                level.setRaining(false)
                level.setThundering(false)
                level.setRainTime(seconds * 20)
                level.setThunderTime(seconds * 20)
                Command.broadcastCommandMessage(sender,
                        TranslationContainer("commands.weather.clear"))
                true
            }
            "rain" -> {
                level.setRaining(true)
                level.setRainTime(seconds * 20)
                Command.broadcastCommandMessage(sender,
                        TranslationContainer("commands.weather.rain"))
                true
            }
            "thunder" -> {
                level.setThundering(true)
                level.setRainTime(seconds * 20)
                level.setThunderTime(seconds * 20)
                Command.broadcastCommandMessage(sender,
                        TranslationContainer("commands.weather.thunder"))
                true
            }
            else -> {
                sender.sendMessage(TranslationContainer("commands.weather.usage", this.usageMessage))
                false
            }
        }
    }

    init {
        this.setPermission("nukkit.command.weather")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newEnum("type", CommandEnum("WeatherType", "clear", "rain", "thunder")),
                CommandParameter.newType("duration", true, CommandParamType.INT)
        ))
    }
}