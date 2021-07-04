package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/9
 */
class GiveCommand(name: String?) : VanillaCommand(name, "%nukkit.command.give.description", "%nukkit.command.give.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 2) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        val player: Player = if (sender is Player && "@p".equals(args[0])) sender as Player else sender.getServer().getPlayer(args[0])
        val item: Item
        item = try {
            Item.fromString(args[1])
        } catch (e: Exception) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        if (item.getDamage() < 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        if (item is ItemBlock && item.getBlock() is BlockUnknown) {
            sender.sendMessage(TranslationContainer("commands.give.block.notFound", args[1]))
            return true
        }
        val count: Int
        count = try {
            if (args.size <= 2) {
                1
            } else {
                Integer.parseInt(args[2])
            }
        } catch (e: NumberFormatException) {
            1
        }
        if (count <= 0) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        item.setCount(count)
        if (player == null) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.generic.player.notFound"))
            return true
        }
        if (item.isNull()) {
            sender.sendMessage(TranslationContainer(TextFormat.RED.toString() + "%commands.give.item.notFound", args[1]))
            return true
        }
        val returns: Array<Item> = player.getInventory().addItem(item.clone())
        val drops: List<Item> = ArrayList()
        for (returned in returns) {
            val maxStackSize: Int = returned.getMaxStackSize()
            if (returned.getCount() <= maxStackSize) {
                drops.add(returned)
            } else {
                while (returned.getCount() > maxStackSize) {
                    val drop: Item = returned.clone()
                    val toDrop: Int = Math.min(returned.getCount(), maxStackSize)
                    drop.setCount(toDrop)
                    returned.setCount(returned.getCount() - toDrop)
                    drops.add(drop)
                }
                if (!returned.isNull()) {
                    drops.add(returned)
                }
            }
        }
        for (drop in drops) {
            player.dropItem(drop)
        }
        Command.broadcastCommandMessage(sender, TranslationContainer(
                "%commands.give.success",
                item.getName().toString() + " (" + item.getId() + ":" + item.getDamage() + ")",
                String.valueOf(item.getCount()),
                player.getName()))
        return true
    }

    init {
        this.setPermission("nukkit.command.give")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        ))
        this.commandParameters.put("toPlayerById", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("itemId", CommandParamType.INT),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        ))
        this.commandParameters.put("toPlayerByIdMeta", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("itemAndData", CommandParamType.STRING),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("tags", true, CommandParamType.RAWTEXT)
        ))
    }
}