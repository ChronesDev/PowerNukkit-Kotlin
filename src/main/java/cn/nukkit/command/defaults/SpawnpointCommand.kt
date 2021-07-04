package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/13
 */
class SpawnpointCommand(name: String?) : VanillaCommand(name, "%nukkit.command.spawnpoint.description", "%commands.spawnpoint.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        val target: Player
        if (args.size == 0) {
            target = if (sender is Player) {
                sender as Player
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.ingame"))
                return true
            }
        } else {
            target = sender.getServer().getPlayer(args[0])
            if (target == null) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
                return true
            }
        }
        val level: Level = target.getLevel()
        val round2 = DecimalFormat("##0.00")
        if (args.size == 4) {
            if (level != null) {
                val x: Int
                var y: Int
                val z: Int
                try {
                    x = Integer.parseInt(args[1])
                    y = Integer.parseInt(args[2])
                    z = Integer.parseInt(args[3])
                } catch (e1: NumberFormatException) {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return true
                }
                if (y < 0) y = 0
                if (y > 256) y = 256
                target.setSpawn(Position(x, y, z, level))
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.spawnpoint.success", target.getName(),
                        round2.format(x),
                        round2.format(y),
                        round2.format(z)))
                return true
            }
        } else if (args.size <= 1) {
            return if (sender is Player) {
                val pos: Position = sender as Position
                target.setSpawn(pos)
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.spawnpoint.success", target.getName(),
                        round2.format(pos.x),
                        round2.format(pos.y),
                        round2.format(pos.z)))
                true
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.ingame"))
                true
            }
        }
        sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
        return true
    }

    init {
        this.setPermission("nukkit.command.spawnpoint")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", true, CommandParamType.TARGET),
                CommandParameter.newType("spawnPos", true, CommandParamType.POSITION)))
    }
}