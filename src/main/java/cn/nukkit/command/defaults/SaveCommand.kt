package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/13
 */
class SaveCommand(name: String?) : VanillaCommand(name, "%nukkit.command.save.description", "%commands.save.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.save.start"))
        for (player in sender.getServer().getOnlinePlayers().values()) {
            player.save()
        }
        for (level in sender.getServer().getLevels().values()) {
            level.save(true)
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.save.success"))
        return true
    }

    init {
        this.setPermission("nukkit.command.save.perform")
        this.commandParameters.clear()
    }
}