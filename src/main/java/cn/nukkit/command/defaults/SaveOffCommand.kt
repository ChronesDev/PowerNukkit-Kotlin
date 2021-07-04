package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author xtypr
 * @since 2015/11/13
 */
class SaveOffCommand(name: String?) : VanillaCommand(name, "%nukkit.command.saveoff.description", "%commands.save-off.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        sender.getServer().setAutoSave(false)
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.save.disabled"))
        return true
    }

    init {
        this.setPermission("nukkit.command.save.disable")
        this.commandParameters.clear()
    }
}