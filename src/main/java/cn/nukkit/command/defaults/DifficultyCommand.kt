package cn.nukkit.command.defaults

import cn.nukkit.Server

/**
 * @author xtypr
 * @since 2015/11/12
 */
class DifficultyCommand(name: String?) : VanillaCommand(name, "%nukkit.command.difficulty.description", "%commands.difficulty.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size != 1) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        var difficulty: Int = Server.getDifficultyFromString(args[0])
        if (sender.getServer().isHardcore()) {
            difficulty = 3
        }
        if (difficulty != -1) {
            sender.getServer().setPropertyInt("difficulty", difficulty)
            val pk = SetDifficultyPacket()
            pk.difficulty = sender.getServer().getDifficulty()
            Server.broadcastPacket(ArrayList(sender.getServer().getOnlinePlayers().values()), pk)
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.difficulty.success", String.valueOf(difficulty)))
        } else {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.difficulty")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("difficulty", CommandParamType.INT)
        ))
        this.commandParameters.put("byString", arrayOf<CommandParameter>(
                CommandParameter.newEnum("difficulty", CommandEnum("Difficulty", "peaceful", "p", "easy", "e", "normal", "n", "hard", "h"))
        ))
    }
}