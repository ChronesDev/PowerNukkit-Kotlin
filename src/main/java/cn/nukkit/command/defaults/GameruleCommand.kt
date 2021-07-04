package cn.nukkit.command.defaults

import cn.nukkit.Player

class GameruleCommand(name: String?) : VanillaCommand(name, "%nukkit.command.gamerule.description", "%nukkit.command.gamerule.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (!sender.isPlayer()) {
            sender.sendMessage(TranslationContainer("%commands.generic.ingame"))
            return true
        }
        val rules: GameRules = (sender as Player).getLevel().getGameRules()
        return when (args.size) {
            0 -> {
                val rulesJoiner = StringJoiner(", ")
                for (rule in rules.getRules()) {
                    rulesJoiner.add(rule.getName().toLowerCase())
                }
                sender.sendMessage(rulesJoiner.toString())
                true
            }
            1 -> {
                val gameRule: Optional<GameRule> = GameRule.parseString(args[0])
                if (!gameRule.isPresent() || !rules.hasRule(gameRule.get())) {
                    sender.sendMessage(TranslationContainer("commands.generic.syntax", "/gamerule", args[0]))
                    return true
                }
                sender.sendMessage(gameRule.get().getName().toLowerCase().toString() + " = " + rules.getString(gameRule.get()))
                true
            }
            else -> {
                val optionalRule: Optional<GameRule> = GameRule.parseString(args[0])
                if (!optionalRule.isPresent()) {
                    sender.sendMessage(TranslationContainer("commands.generic.syntax",
                            "/gamerule ", args[0], " " + String.join(" ", Arrays.copyOfRange(args, 1, args.size))))
                    return true
                }
                try {
                    rules.setGameRules(optionalRule.get(), args[1])
                    sender.sendMessage(TranslationContainer("commands.gamerule.success", optionalRule.get().getName().toLowerCase(), args[1]))
                } catch (e: IllegalArgumentException) {
                    sender.sendMessage(TranslationContainer("commands.generic.syntax", "/gamerule " + args[0] + " ", args[1], " " + String.join(" ", Arrays.copyOfRange(args, 2, args.size))))
                }
                true
            }
        }
    }

    init {
        this.setPermission("nukkit.command.gamerule")
        this.commandParameters.clear()
        val rules: GameRules = GameRules.getDefault()
        val boolGameRules: List<String> = ArrayList()
        val intGameRules: List<String> = ArrayList()
        val floatGameRules: List<String> = ArrayList()
        val unknownGameRules: List<String> = ArrayList()
        rules.getGameRules().forEach { rule, value ->
            when (value.getType()) {
                BOOLEAN -> boolGameRules.add(rule.getName().toLowerCase())
                INTEGER -> intGameRules.add(rule.getName().toLowerCase())
                FLOAT -> floatGameRules.add(rule.getName().toLowerCase())
                UNKNOWN -> unknownGameRules.add(rule.getName().toLowerCase())
                else -> unknownGameRules.add(rule.getName().toLowerCase())
            }
        }
        if (!boolGameRules.isEmpty()) {
            this.commandParameters.put("boolGameRules", arrayOf<CommandParameter>(
                    CommandParameter.newEnum("rule", CommandEnum("BoolGameRule", boolGameRules)),
                    CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN)
            ))
        }
        if (!intGameRules.isEmpty()) {
            this.commandParameters.put("intGameRules", arrayOf<CommandParameter>(
                    CommandParameter.newEnum("rule", CommandEnum("IntGameRule", intGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.INT)
            ))
        }
        if (!floatGameRules.isEmpty()) {
            this.commandParameters.put("floatGameRules", arrayOf<CommandParameter>(
                    CommandParameter.newEnum("rule", CommandEnum("FloatGameRule", floatGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.FLOAT)
            ))
        }
        if (!unknownGameRules.isEmpty()) {
            this.commandParameters.put("unknownGameRules", arrayOf<CommandParameter>(
                    CommandParameter.newEnum("rule", CommandEnum("UnknownGameRule", unknownGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.STRING)
            ))
        }
    }
}