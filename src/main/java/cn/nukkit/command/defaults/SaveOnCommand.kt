package cn.nukkit.command.defaults

import cn.nukkit.command.Command

/**
 * @author xtypr
 * @since 2015/11/13
 */
class SaveOnCommand(name: String?) : VanillaCommand(name, "%nukkit.command.saveon.description", "%commands.save-on.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        sender.getServer().setAutoSave(true)
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.save.enabled"))
        return true
    }

    init {
        this.setPermission("nukkit.command.save.enable")
        this.commandParameters.clear()
    }
}