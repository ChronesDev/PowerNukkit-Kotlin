package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Snake1999 and Pub4Game
 * @since 2016/1/23
 */
class EffectCommand(name: String?) : Command(name, "%nukkit.command.effect.description", "%commands.effect.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        val player: Player = sender.getServer().getPlayer(args[0])
        if (player == null) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
            return true
        }
        if (args[1].equalsIgnoreCase("clear")) {
            for (effect in player.getEffects().values()) {
                player.removeEffect(effect.getId())
            }
            sender.sendMessage(TranslationContainer("commands.effect.success.removed.all", player.getDisplayName()))
            return true
        }
        val effect: Effect
        effect = try {
            Effect.getEffect(Integer.parseInt(args[1]))
        } catch (a: NumberFormatException) {
            try {
                Effect.getEffectByName(args[1])
            } catch (e: Exception) {
                sender.sendMessage(TranslationContainer("commands.effect.notFound", args[1]))
                return true
            }
        } catch (a: ServerException) {
            try {
                Effect.getEffectByName(args[1])
            } catch (e: Exception) {
                sender.sendMessage(TranslationContainer("commands.effect.notFound", args[1]))
                return true
            }
        }
        var duration = 300
        var amplification = 0
        if (args.size >= 3) {
            duration = try {
                Integer.parseInt(args[2])
            } catch (a: NumberFormatException) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
            if (effect !is InstantEffect) {
                duration *= 20
            }
        } else if (effect is InstantEffect) {
            duration = 1
        }
        if (args.size >= 4) {
            amplification = try {
                Integer.parseInt(args[3])
            } catch (a: NumberFormatException) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
        }
        if (args.size >= 5) {
            val v: String = args[4].toLowerCase()
            if (v.matches("(?i)|on|true|t|1")) {
                effect.setVisible(false)
            }
        }
        if (duration == 0) {
            if (!player.hasEffect(effect.getId())) {
                if (player.getEffects().size() === 0) {
                    sender.sendMessage(TranslationContainer("commands.effect.failure.notActive.all", player.getDisplayName()))
                } else {
                    sender.sendMessage(TranslationContainer("commands.effect.failure.notActive", effect.getName(), player.getDisplayName()))
                }
                return true
            }
            player.removeEffect(effect.getId())
            sender.sendMessage(TranslationContainer("commands.effect.success.removed", effect.getName(), player.getDisplayName()))
        } else {
            effect.setDuration(duration).setAmplifier(amplification)
            player.addEffect(effect)
            Command.broadcastCommandMessage(sender, TranslationContainer("%commands.effect.success", effect.getName(), String.valueOf(effect.getAmplifier()), player.getDisplayName(), String.valueOf(effect.getDuration() / 20)))
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.effect")
        this.commandParameters.clear()
        val effects: List<String> = ArrayList()
        for (field in Effect::class.java.getDeclaredFields()) {
            if (field.getType() === Int::class.javaPrimitiveType && field.getModifiers() === Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL) {
                effects.add(field.getName().toLowerCase())
            }
        }
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("effect", CommandEnum("Effect", effects)),
                CommandParameter.newType("seconds", true, CommandParamType.INT),
                CommandParameter.newType("amplifier", true, CommandParamType.INT),
                CommandParameter.newEnum("hideParticle", true, CommandEnum.ENUM_BOOLEAN)
        ))
        this.commandParameters.put("clear", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("clear", CommandEnum("ClearEffects", "clear"))
        ))
    }
}