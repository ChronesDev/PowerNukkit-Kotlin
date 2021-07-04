package cn.nukkit.command.defaults

import cn.nukkit.Player

class SetBlockCommand(name: String?) : VanillaCommand(name, "%nukkit.command.setblock.description", "%nukkit.command.setblock.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 4) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        if (sender !is Player) {
            sender.sendMessage(TranslationContainer("commands.setblock.outOfWorld"))
            return true
        }
        val player: Player = sender as Player
        val x: Double
        val y: Double
        val z: Double
        var data = 0
        try {
            x = parseTilde(args[0], player.x)
            y = parseTilde(args[1], player.y)
            z = parseTilde(args[2], player.z)
            if (args.size > 4) {
                data = Integer.parseInt(args[4])
            }
        } catch (ignored: NumberFormatException) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        } catch (ignored: IndexOutOfBoundsException) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        var oldBlockHandling: String? = "replace"
        if (args.size > 5) {
            oldBlockHandling = args[5].toLowerCase()
            when (oldBlockHandling) {
                "destroy", "keep", "replace" -> {
                }
                else -> {
                    sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
                    return true
                }
            }
        }
        val block: Block
        block = try {
            val blockId: Int = Integer.parseInt(args[3])
            Block.get(blockId, data)
        } catch (ignored: NullPointerException) {
            try {
                val blockId: Int = BlockID::class.java.getField(args[3].toUpperCase()).getInt(null)
                Block.get(blockId, data)
            } catch (ignored2: NullPointerException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: IndexOutOfBoundsException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: ReflectiveOperationException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            }
        } catch (ignored: NumberFormatException) {
            try {
                val blockId: Int = BlockID::class.java.getField(args[3].toUpperCase()).getInt(null)
                Block.get(blockId, data)
            } catch (ignored2: NullPointerException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: IndexOutOfBoundsException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: ReflectiveOperationException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            }
        } catch (ignored: IndexOutOfBoundsException) {
            try {
                val blockId: Int = BlockID::class.java.getField(args[3].toUpperCase()).getInt(null)
                Block.get(blockId, data)
            } catch (ignored2: NullPointerException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: IndexOutOfBoundsException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            } catch (ignored2: ReflectiveOperationException) {
                sender.sendMessage(TranslationContainer("commands.setblock.notFound", args[3]))
                return true
            }
        }
        if (y < 0 || y > 255) {
            sender.sendMessage(TranslationContainer("commands.setblock.outOfWorld"))
            return true
        }
        val level: Level = player.getLevel()
        val position = Position(x, y, z, player.getLevel())
        var current: Block = level.getBlock(position)
        if (current.getId() !== Block.AIR) {
            when (oldBlockHandling) {
                "destroy" -> {
                    level.useBreakOn(position, null, Item.get(Item.AIR), player, true, true)
                    current = level.getBlock(position)
                }
                "keep" -> {
                    sender.sendMessage(TranslationContainer("commands.setblock.noChange"))
                    return true
                }
            }
        }
        if (current.getId() === block.getId() && current.getDamage() === block.getDamage()) {
            sender.sendMessage(TranslationContainer("commands.setblock.noChange"))
            return true
        }
        val item: Item = block.toItem()
        block.position(position)
        if (block.place(item, block, block.down(), BlockFace.UP, 0.5, 0.5, 0.5, player)) {
            if (args.size > 4) {
                level.setBlockDataAt(x.toInt(), y.toInt(), z.toInt(), data)
            }
            //if (level.setBlock(position, block, true, true)) {
            sender.sendMessage(TranslationContainer("commands.setblock.success"))
        } else {
            sender.sendMessage(TranslationContainer("commands.setblock.failed"))
        }
        return true
    }

    init {
        this.setPermission("nukkit.command.setblock")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter("position", CommandParamType.POSITION, false),
                CommandParameter("tileName", false, Arrays.stream(BlockID::class.java.getDeclaredFields()).map { f -> f.getName().toLowerCase() }.toArray { _Dummy_.__Array__() }),
                CommandParameter("tileData", CommandParamType.INT, true),
                CommandParameter("oldBlockHandling", true, arrayOf("destroy", "keep", "replace"))
        ))
    }
}