package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 23.01.2016
 */
class EnchantCommand(name: String?) : VanillaCommand(name, "%nukkit.command.enchant.description", "%commands.enchant.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String?>): Boolean {
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
        val enchantId: Int
        val enchantLevel: Int
        try {
            enchantId = getIdByName(args[1])
            enchantLevel = if (args.size == 3) Integer.parseInt(args[2]) else 1
        } catch (e: NumberFormatException) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        val enchantment: Enchantment = Enchantment.getEnchantment(enchantId)
        if (enchantment == null) {
            sender.sendMessage(TranslationContainer("commands.enchant.notFound", String.valueOf(enchantId)))
            return true
        }
        enchantment.setLevel(enchantLevel)
        val item: Item = player.getInventory().getItemInHand()
        if (item.getId() === 0) {
            sender.sendMessage(TranslationContainer("commands.enchant.noItem"))
            return true
        }
        if (item.getId() !== ItemID.BOOK) {
            item.addEnchantment(enchantment)
            player.getInventory().setItemInHand(item)
        } else {
            val enchanted: Item = Item.get(ItemID.ENCHANTED_BOOK, 0, 1, item.getCompoundTag())
            enchanted.addEnchantment(enchantment)
            val clone: Item = item.clone()
            clone.count--
            val inventory: PlayerInventory = player.getInventory()
            inventory.setItemInHand(clone)
            player.giveItem(enchanted)
        }
        Command.broadcastCommandMessage(sender, TranslationContainer("%commands.enchant.success"))
        return true
    }

    @Throws(NumberFormatException::class)
    fun getIdByName(value: String?): Int {
        return when (value) {
            "protection" -> 0
            "fire_protection" -> 1
            "feather_falling" -> 2
            "blast_protection" -> 3
            "projectile_projection" -> 4
            "thorns" -> 5
            "respiration" -> 6
            "aqua_affinity" -> 7
            "depth_strider" -> 8
            "sharpness" -> 9
            "smite" -> 10
            "bane_of_arthropods" -> 11
            "knockback" -> 12
            "fire_aspect" -> 13
            "looting" -> 14
            "efficiency" -> 15
            "silk_touch" -> 16
            "durability" -> 17
            "fortune" -> 18
            "power" -> 19
            "punch" -> 20
            "flame" -> 21
            "infinity" -> 22
            "luck_of_the_sea" -> 23
            "lure" -> 24
            "frost_walker" -> 25
            "mending" -> 26
            "binding_curse" -> 27
            "vanishing_curse" -> 28
            "impaling" -> 29
            "loyality" -> 30
            "riptide" -> 31
            "channeling" -> 32
            "multishot" -> 33
            "piercing" -> 34
            "quick_charge" -> 35
            "soul_speed" -> 36
            else -> Integer.parseInt(value)
        }
    }

    init {
        this.setPermission("nukkit.command.enchant")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        ))
        this.commandParameters.put("byName", arrayOf<CommandParameter>(
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", CommandEnum("Enchant",
                        "protection", "fire_protection", "feather_falling", "blast_protection", "projectile_projection", "thorns", "respiration",
                        "aqua_affinity", "depth_strider", "sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect", "looting", "efficiency",
                        "silk_touch", "durability", "fortune", "power", "punch", "flame", "infinity", "luck_of_the_sea", "lure", "frost_walker", "mending",
                        "binding_curse", "vanishing_curse", "impaling", "loyality", "riptide", "channeling", "multishot", "piercing", "quick_charge",
                        "soul_speed")),
                CommandParameter.newType("level", true, CommandParamType.INT)
        ))
    }
}