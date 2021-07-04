package cn.nukkit.command.defaults

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/12
 */
class ParticleCommand(name: String?) : VanillaCommand(name, "%nukkit.command.particle.description", "%nukkit.command.particle.usage") {
    @Override
    fun execute(sender: CommandSender, commandLabel: String?, args: Array<String>): Boolean {
        if (!this.testPermission(sender)) {
            return true
        }
        if (args.size < 4) {
            sender.sendMessage(TranslationContainer("commands.generic.usage", this.usageMessage))
            return true
        }
        val defaultPosition: Position
        if (sender is Player) {
            defaultPosition = (sender as Player).getPosition()
        } else {
            defaultPosition = Position(0, 0, 0, sender.getServer().getDefaultLevel())
        }
        val name: String = args[0].toLowerCase()
        val x: Double
        val y: Double
        val z: Double
        try {
            x = parseTilde(args[1], defaultPosition.getX())
            y = parseTilde(args[2], defaultPosition.getY())
            z = parseTilde(args[3], defaultPosition.getZ())
        } catch (e: Exception) {
            return false
        }
        val position = Position(x, y, z, defaultPosition.getLevel())
        var count = 1
        if (args.size > 4) {
            try {
                val c: Double = Double.parseDouble(args[4])
                count = c.toInt()
            } catch (e: Exception) {
                //ignore
            }
        }
        count = Math.max(1, count)
        var data = -1
        if (args.size > 5) {
            try {
                val d: Double = Double.parseDouble(args[5])
                data = d.toInt()
            } catch (e: Exception) {
                //ignore
            }
        }
        val particle: Particle? = getParticle(name, position, data)
        if (particle == null) {
            position.level.addParticleEffect(position.asVector3f(), args[0], -1, position.level.getDimension())
            return true
        }
        sender.sendMessage(TranslationContainer("commands.particle.success", name, String.valueOf(count)))
        val random: Random = ThreadLocalRandom.current()
        for (i in 0 until count) {
            particle.setComponents(
                    position.x + (random.nextFloat() * 2 - 1),
                    position.y + (random.nextFloat() * 2 - 1),
                    position.z + (random.nextFloat() * 2 - 1)
            )
            position.getLevel().addParticle(particle)
        }
        return true
    }

    private fun getParticle(name: String, pos: Vector3, data: Int): Particle? {
        when (name) {
            "explode" -> return ExplodeParticle(pos)
            "hugeexplosion" -> return HugeExplodeParticle(pos)
            "hugeexplosionseed" -> return HugeExplodeSeedParticle(pos)
            "bubble" -> return BubbleParticle(pos)
            "splash" -> return SplashParticle(pos)
            "wake", "water" -> return WaterParticle(pos)
            "crit" -> return CriticalParticle(pos)
            "smoke" -> return SmokeParticle(pos, if (data != -1) data else 0)
            "spell" -> return EnchantParticle(pos)
            "instantspell" -> return InstantEnchantParticle(pos)
            "dripwater" -> return WaterDripParticle(pos)
            "driplava" -> return LavaDripParticle(pos)
            "townaura", "spore" -> return SporeParticle(pos)
            "portal" -> return PortalParticle(pos)
            "flame" -> return FlameParticle(pos)
            "lava" -> return LavaParticle(pos)
            "reddust" -> return RedstoneParticle(pos, if (data != -1) data else 1)
            "snowballpoof" -> return ItemBreakParticle(pos, Item.get(Item.SNOWBALL))
            "slime" -> return ItemBreakParticle(pos, Item.get(Item.SLIMEBALL))
            "itembreak" -> if (data != -1 && data != 0) {
                return ItemBreakParticle(pos, Item.get(data))
            }
            "terrain" -> if (data != -1 && data != 0) {
                return TerrainParticle(pos, Block.get(data))
            }
            "heart" -> return HeartParticle(pos, if (data != -1) data else 0)
            "ink" -> return InkParticle(pos, if (data != -1) data else 0)
            "droplet" -> return RainSplashParticle(pos)
            "enchantmenttable" -> return EnchantmentTableParticle(pos)
            "happyvillager" -> return HappyVillagerParticle(pos)
            "angryvillager" -> return AngryVillagerParticle(pos)
            "forcefield" -> return BlockForceFieldParticle(pos)
        }
        if (name.startsWith("iconcrack_")) {
            val d: Array<String> = name.split("_")
            if (d.size == 3) {
                return ItemBreakParticle(pos, Item.get(Integer.parseInt(d[1]), Integer.valueOf(d[2])))
            }
        } else if (name.startsWith("blockcrack_")) {
            val d: Array<String> = name.split("_")
            if (d.size == 2) {
                return TerrainParticle(pos, Block.get(Integer.parseInt(d[1]) and 0xff, Integer.parseInt(d[1]) shr 12))
            }
        } else if (name.startsWith("blockdust_")) {
            val d: Array<String> = name.split("_")
            if (d.size >= 4) {
                return DustParticle(pos, Integer.parseInt(d[1]) and 0xff, Integer.parseInt(d[2]) and 0xff, Integer.parseInt(d[3]) and 0xff, if (d.size >= 5) Integer.parseInt(d[4]) and 0xff else 255)
            }
        }
        return null
    }

    companion object {
        private val ENUM_VALUES = arrayOf("explode", "hugeexplosion", "hugeexplosionseed", "bubble", "splash", "wake", "water", "crit", "smoke", "spell", "instantspell", "dripwater", "driplava", "townaura", "spore", "portal", "flame", "lava", "reddust", "snowballpoof", "slime", "itembreak", "terrain", "heart", "ink", "droplet", "enchantmenttable", "happyvillager", "angryvillager", "forcefield")
    }

    init {
        this.setPermission("nukkit.command.particle")
        this.commandParameters.clear()
        this.commandParameters.put("default", arrayOf<CommandParameter>(
                CommandParameter.newEnum("effect", CommandEnum("Particle", ENUM_VALUES)),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("count", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT)
        ))
    }
}