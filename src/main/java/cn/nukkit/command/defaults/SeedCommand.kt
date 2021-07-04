package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class SeedCommand(name: String?) : VanillaCommand(name, "%nukkit.command.seed.description", "%commands.seed.usage") {
    @Override
    override fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>?): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        val seed: Long
        seed = if (sender is Player) {
            (sender as Player).getLevel().getSeed()
        } else {
            sender.getServer().getDefaultLevel().getSeed()
        }
        sender.sendMessage(TranslationContainer("commands.seed.success", String.valueOf(seed)))
        return true
    }

    init {
        this.setPermission("nukkit.command.seed")
        this.commandParameters.clear()
    }
}