package cn.nukkit.level

import cn.nukkit.Server

@Log4j2
enum class EnumLevel {
    OVERWORLD, NETHER;

    //THE_END
    var level: Level? = null
    fun getLevel(): Level? {
        return level
    }

    companion object {
        fun initLevels() {
            OVERWORLD.level = Server.getInstance().getDefaultLevel()

            // attempt to load the nether world if it is allowed in server properties
            if (Server.getInstance().isNetherAllowed() && !Server.getInstance().loadLevel("nether")) {

                // Nether is allowed, and not found, create the default nether world
                log.info("No level called \"nether\" found, creating default nether level.")

                // Generate seed for nether and get nether generator
                val seed: Long = System.currentTimeMillis()
                val generator: Class<out Generator?> = Generator.getGenerator("nether")

                // Generate the nether world
                Server.getInstance().generateLevel("nether", seed, generator)

                // Finally, load the level if not already loaded and set the level
                if (!Server.getInstance().isLevelLoaded("nether")) {
                    Server.getInstance().loadLevel("nether")
                }
            }
            NETHER.level = Server.getInstance().getLevelByName("nether")
            if (NETHER.level == null) {
                // Nether is not found or disabled
                log.warn("No level called \"nether\" found or nether is disabled in server properties! Nether functionality will be disabled.")
            }
        }

        fun getOtherNetherPair(current: Level): Level? {
            return if (current === OVERWORLD.level) {
                NETHER.level
            } else if (current === NETHER.level) {
                OVERWORLD.level
            } else {
                throw IllegalArgumentException("Neither overworld nor nether given!")
            }
        }

        fun moveToNether(current: Position): Position? {
            return if (NETHER.level == null) {
                null
            } else {
                if (current.level === OVERWORLD.level) {
                    Position(current.getFloorX() shr 3, NukkitMath.clamp(current.getFloorY(), 70, 118), current.getFloorZ() shr 3, NETHER.level)
                } else if (current.level === NETHER.level) {
                    Position(current.getFloorX() shl 3, NukkitMath.clamp(current.getFloorY(), 70, 246), current.getFloorZ() shl 3, OVERWORLD.level)
                } else {
                    throw IllegalArgumentException("Neither overworld nor nether given!")
                }
            }
        }

        private fun mRound(value: Int, factor: Int): Int {
            return Math.round(value.toFloat() / factor) * factor
        }
    }
}