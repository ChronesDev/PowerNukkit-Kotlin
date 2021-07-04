package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ReloadCommand(name: String?) : VanillaCommand(name, "%nukkit.command.reload.description", "%commands.reload.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        Command.broadcastCommandMessage(sender, TranslationContainer(TextFormat.YELLOW.toString() + "%nukkit.command.reload.reloading" + TextFormat.WHITE))
        sender.getServer().reload()
        Command.broadcastCommandMessage(sender, TranslationContainer(TextFormat.YELLOW.toString() + "%nukkit.command.reload.reloaded" + TextFormat.WHITE))
        return true
    }

    init {
        this.setPermission("nukkit.command.reload")
        this.commandParameters.clear()
    }
}