package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Pub4Game and milkice
 * @since 2015/11/12
 */
class TeleportCommand(name: String?) : VanillaCommand(name, "%nukkit.command.tp.description", "%commands.tp.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 1 || args.size > 6) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        var target: CommandSender
        var origin: CommandSender = sender
        if (args.size == 1 || args.size == 3 || args.size == 5) {
            target = if (sender is Player) {
                sender
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.ingame"))
                return true
            }
            if (args.size == 1) {
                target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()))
                if (target == null) {
                    sender.sendMessage(TextFormat.RED.toString() + "Can't find player " + args[0])
                    return true
                }
            }
        } else {
            target = sender.getServer().getPlayer(args[0].replace("@s", sender.getName()))
            if (target == null) {
                sender.sendMessage(TextFormat.RED.toString() + "Can't find player " + args[0])
                return true
            }
            if (args.size == 2) {
                origin = target
                target = sender.getServer().getPlayer(args[1].replace("@s", sender.getName()))
                if (target == null) {
                    sender.sendMessage(TextFormat.RED.toString() + "Can't find player " + args[1])
                    return true
                }
            }
        }
        if (args.size < 3) {
            (origin as Player).teleport(target as Player, PlayerTeleportEvent.TeleportCause.COMMAND)
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.tp.success", origin.getName(), target.getName()))
            return true
        } else if ((target as Player).getLevel() != null) {
            var pos: Int
            pos = if (args.size == 4 || args.size == 6) {
                1
            } else {
                0
            }
            val x: Double
            val y: Double
            val z: Double
            val yaw: Double
            val pitch: Double
            try {
                x = parseTilde(args[pos++], (target as Player).x)
                y = parseTilde(args[pos++], (target as Player).y)
                z = parseTilde(args[pos++], (target as Player).z)
                if (args.size > pos) {
                    yaw = Integer.parseInt(args[pos++])
                    pitch = Integer.parseInt(args[pos])
                } else {
                    yaw = (target as Player).getYaw()
                    pitch = (target as Player).getPitch()
                }
            } catch (e1: NumberFormatException) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
            (target as Player).teleport(Location(x, y, z, yaw, pitch, (target as Player).getLevel()), PlayerTeleportEvent.TeleportCause.COMMAND)
            Command.broadcastCommandMessage(sender, TranslationContainer("commands.tp.success.coordinates", target.getName(), String.valueOf(NukkitMath.round(x, 2)), String.valueOf(NukkitMath.round(y, 2)), String.valueOf(NukkitMath.round(z, 2))))
            return true
        }
        sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
        return true
    }

    init {
        this.setPermission("nukkit.command.teleport")
        this.commandParameters.clear()
        this.commandParameters.put("->Player", arrayOf<CommandParameter>(
                CommandParameter.newType("destination", CommandParamType.TARGET)))
        this.commandParameters.put("Player->Player", arrayOf<CommandParameter>(
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.TARGET)
        ))
        this.commandParameters.put("Player->Pos", arrayOf<CommandParameter>(
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        ))
        this.commandParameters.put("->Pos", arrayOf<CommandParameter>(
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE)
        ))
    }
}