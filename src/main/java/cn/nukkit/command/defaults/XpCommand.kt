package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/22
 */
class XpCommand(name: String?) : Command(name, "%nukkit.command.xp.description", "%commands.xp.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }

        //  "/xp <amount> [player]"  for adding exp
        //  "/xp <amount>L [player]" for adding exp level
        var amountString: String
        val playerName: String
        val player: Player
        if (sender !is Player) {
            if (args.size != 2) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
            amountString = args[0]
            playerName = args[1]
            player = sender.getServer().getPlayer(playerName)
        } else {
            if (args.size == 1) {
                amountString = args[0]
                player = sender as Player
            } else if (args.size == 2) {
                amountString = args[0]
                playerName = args[1]
                player = sender.getServer().getPlayer(playerName)
            } else {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
        }
        if (player == null) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
            return true
        }
        val amount: Int
        var isLevel = false
        if (amountString.endsWith("l") || amountString.endsWith("L")) {
            amountString = amountString.substring(0, amountString.length() - 1)
            isLevel = true
        }
        amount = try {
            Integer.parseInt(amountString)
        } catch (e1: NumberFormatException) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        return if (isLevel) {
            var newLevel: Int = player.getExperienceLevel()
            newLevel += amount
            if (newLevel > 24791) newLevel = 24791
            if (newLevel < 0) {
                player.setExperience(0, 0)
            } else {
                player.setExperience(player.getExperience(), newLevel, true)
            }
            if (amount > 0) {
                sender.sendMessage(TranslationContainer("commands.xp.success.levels", String.valueOf(amount), player.getName()))
            } else {
                sender.sendMessage(TranslationContainer("commands.xp.success.levels.minus", String.valueOf(-amount), player.getName()))
            }
            true
        } else {
            if (amount < 0) {
                sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                return true
            }
            player.addExperience(amount)
            sender.sendMessage(TranslationContainer("commands.xp.success", String.valueOf(amount), player.getName()))
            true
        }
    }

    init {
        this.setPermission("nukkit.command.xp")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("amount", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        ))
        this.commandParameters.put("level", arrayOf<CommandParameter>(
                CommandParameter.newPostfix("amount", "l"),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        ))
    }
}