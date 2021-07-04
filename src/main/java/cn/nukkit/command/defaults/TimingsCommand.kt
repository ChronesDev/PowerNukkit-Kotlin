package cn.nukkit.command.defaults

import cn.nukkit.command.CommandSender

/**
 * @author fromgate
 * @author Pub4Game
 */
class TimingsCommand(name: String?) : VanillaCommand(name, "%nukkit.command.timings.description", "%nukkit.command.timings.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size != 1) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", usageMessage))
            return true
        }
        val mode: String = args[0].toLowerCase()
        if (mode.equals("on")) {
            Timings.setTimingsEnabled(true)
            Timings.reset()
            sender.sendMessage(TranslationContainer("nukkit.command.timings.enable"))
            return true
        } else if (mode.equals("off")) {
            Timings.setTimingsEnabled(false)
            sender.sendMessage(TranslationContainer("nukkit.command.timings.disable"))
            return true
        }
        if (!Timings.isTimingsEnabled()) {
            sender.sendMessage(TranslationContainer("nukkit.command.timings.timingsDisabled"))
            return true
        }
        when (mode) {
            "verbon" -> {
                sender.sendMessage(TranslationContainer("nukkit.command.timings.verboseEnable"))
                Timings.setVerboseEnabled(true)
            }
            "verboff" -> {
                sender.sendMessage(TranslationContainer("nukkit.command.timings.verboseDisable"))
                Timings.setVerboseEnabled(true)
            }
            "reset" -> {
                Timings.reset()
                sender.sendMessage(TranslationContainer("nukkit.command.timings.reset"))
            }
            "report", "paste" -> TimingsExport.reportTimings(sender)
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.timings")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newEnum("action", CommandEnum("TimingsAction", "on", "off", "paste", "verbon", "verboff", "reset", "report"))
        ))
    }
}