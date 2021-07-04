package cn.nukkit.level

import cn.nukkit.api.PowerNukkitOnly

enum class GameRule(override val name: String) {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"), DO_DAYLIGHT_CYCLE("doDaylightCycle"), DO_ENTITY_DROPS("doEntityDrops"), DO_FIRE_TICK("doFireTick"), DO_IMMEDIATE_RESPAWN("doImmediateRespawn"), DO_MOB_LOOT("doMobLoot"), DO_MOB_SPAWNING("doMobSpawning"), DO_TILE_DROPS("doTileDrops"), DO_WEATHER_CYCLE("doWeatherCycle"), DROWNING_DAMAGE("drowningDamage"), FALL_DAMAGE("fallDamage"), FIRE_DAMAGE("fireDamage"), KEEP_INVENTORY("keepInventory"), MOB_GRIEFING("mobGriefing"), NATURAL_REGENERATION("naturalRegeneration"), PVP("pvp"), RANDOM_TICK_SPEED("randomTickSpeed"), SEND_COMMAND_FEEDBACK("sendCommandFeedback"), SHOW_COORDINATES("showCoordinates"), TNT_EXPLODES("tntExplodes"), SHOW_DEATH_MESSAGE("showDeathMessages"), EXPERIMENTAL_GAMEPLAY("experimentalGameplay"), MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"), DO_INSOMNIA("doInsomnia"), COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"), FUNCTION_COMMAND_LIMIT("functionCommandLimit"), SPAWN_RADIUS("spawnRadius"), SHOW_TAGS("showTags");

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<GameRule>(0)
        fun parseString(gameRuleString: String?): Optional<GameRule> {
            //Backward compatibility
            var gameRuleString = gameRuleString
            if ("showDeathMessage".equalsIgnoreCase(gameRuleString)) {
                gameRuleString = "showDeathMessages"
            }
            for (gameRule in values()) {
                if (gameRule.name.equalsIgnoreCase(gameRuleString)) {
                    return Optional.of(gameRule)
                }
            }
            return Optional.empty()
        }

        val names: Array<String?>
            get() {
                val stringValues = arrayOfNulls<String>(values().size)
                for (i in values().indices) {
                    stringValues[i] = values()[i].name
                }
                return stringValues
            }
    }
}