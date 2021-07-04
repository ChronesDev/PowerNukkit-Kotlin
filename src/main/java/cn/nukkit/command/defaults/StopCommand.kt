package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author MagicDroidX (Nukkit Project)
 */
class StopCommand(name: String?) : VanillaCommand(name, "%nukkit.command.stop.description", "%commands.stop.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.stop.start"))
        sender.getServer().shutdown()
        return true
    }

    init {
        this.setPermission("nukkit.command.stop")
        this.commandParameters.clear()
    }
}