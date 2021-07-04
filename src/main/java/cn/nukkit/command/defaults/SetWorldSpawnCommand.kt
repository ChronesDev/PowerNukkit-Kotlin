package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/13
 */
class SetWorldSpawnCommand(name: String?) : VanillaCommand(name, "%nukkit.command.setworldspawn.description", "%commands.setworldspawn.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        val level: Level
        val pos: Vector3
        if (args.size == 0) {
            if (sender is Player) {
                level = (sender as Player).getLevel()
                pos = (sender as Player).round()
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.ingame"))
                return true
            }
        } else if (args.size == 3) {
            level = sender.getServer().getDefaultLevel()
            try {
                pos = Vector3(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]))
            } catch (e1: NumberFormatException) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
        } else {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        level.setSpawnLocation(pos)
        val round2 = DecimalFormat("##0.00")
        Command.broadcastCommandMessage(sender, TranslationContainer("commands.setworldspawn.success", round2.format(pos.x),
                round2.format(pos.y),
                round2.format(pos.z)))
        return true
    }

    init {
        this.setPermission("nukkit.command.setworldspawn")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("spawnPoint", true, CommandParamType.POSITION)
        ))
    }
}