package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 2015/12/08
 */
class KillCommand(name: String?) : VanillaCommand(name, "%nukkit.command.kill.description", "%nukkit.command.kill.usage", arrayOf("suicide")) {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size >= 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        if (args.size == 1) {
            if (!sender.hasPermission("nukkit.command.kill.other")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            val player: Player = sender.getServer().getPlayer(args[0])
            if (player != null) {
                val ev = EntityDamageEvent(player, DamageCause.SUICIDE, 1000)
                sender.getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return true
                }
                player.setLastDamageCause(ev)
                player.setHealth(0)
                Command.broadcastCommandMessage(sender, TranslationContainer("commands.kill.successful", player.getName()))
            } else if (args[0].equals("@e")) {
                val joiner = StringJoiner(", ")
                for (level in Server.getInstance().getLevels().values()) {
                    for (entity in level.getEntities()) {
                        if (entity !is Player) {
                            joiner.add(entity.getName())
                            entity.close()
                        }
                    }
                }
                val entities: String = joiner.toString()
                sender.sendMessage(TranslationContainer("commands.kill.successful", if (entities.isEmpty()) "0" else entities))
            } else if (args[0].equals("@s")) {
                if (!sender.hasPermission("nukkit.command.kill.self")) {
                    sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                    return true
                }
                val ev = EntityDamageEvent(sender as Player, DamageCause.SUICIDE, 1000)
                sender.getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return true
                }
                (sender as Player).setLastDamageCause(ev)
                (sender as Player).setHealth(0)
                sender.sendMessage(TranslationContainer("commands.kill.successful", sender.getName()))
            } else if (args[0].equals("@a")) {
                if (!sender.hasPermission("nukkit.command.kill.other")) {
                    sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                    return true
                }
                for (level in Server.getInstance().getLevels().values()) {
                    for (entity in level.getEntities()) {
                        if (entity is Player) {
                            entity.setHealth(0)
                        }
                    }
                }
                sender.sendMessage(TranslationContainer(TextFormat.GOLD.toString() + "%commands.kill.all.successful"))
            } else {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
            }
            return true
        }
        if (sender is Player) {
            if (!sender.hasPermission("nukkit.command.kill.self")) {
                sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.permission"))
                return true
            }
            val ev = EntityDamageEvent(sender as Player, DamageCause.SUICIDE, 1000)
            sender.getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return true
            }
            (sender as Player).setLastDamageCause(ev)
            (sender as Player).setHealth(0)
            sender.sendMessage(TranslationContainer("commands.kill.successful", sender.getName()))
        } else {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return false
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        ))
    }
}