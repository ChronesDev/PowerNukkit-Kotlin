package cn.nukkit.level

import cn.nukkit.api.Since

@SuppressWarnings(["unchecked"])
class GameRules private constructor() {
    private val gameRules: EnumMap<GameRule, Value<*>> = EnumMap(GameRule::class.java)
    var isStale = false
        private set

    fun getGameRules(): Map<GameRule, Value<*>> {
        return ImmutableMap.copyOf(gameRules)
    }

    fun refresh() {
        isStale = false
    }

    fun setGameRule(gameRule: GameRule?, value: Boolean) {
        if (!gameRules.containsKey(gameRule)) {
            throw IllegalArgumentException("Gamerule does not exist")
        }
        gameRules.get(gameRule).setValue(value, Type.BOOLEAN)
        isStale = true
    }

    fun setGameRule(gameRule: GameRule?, value: Int) {
        if (!gameRules.containsKey(gameRule)) {
            throw IllegalArgumentException("Gamerule does not exist")
        }
        gameRules.get(gameRule).setValue(value, Type.INTEGER)
        isStale = true
    }

    fun setGameRule(gameRule: GameRule?, value: Float) {
        if (!gameRules.containsKey(gameRule)) {
            throw IllegalArgumentException("Gamerule does not exist")
        }
        gameRules.get(gameRule).setValue(value, Type.FLOAT)
        isStale = true
    }

    @Throws(IllegalArgumentException::class)
    fun setGameRules(gameRule: GameRule?, value: String) {
        Preconditions.checkNotNull(gameRule, "gameRule")
        Preconditions.checkNotNull(value, "value")
        when (getGameRuleType(gameRule)) {
            Type.BOOLEAN -> if (value.equalsIgnoreCase("true")) {
                setGameRule(gameRule, true)
            } else if (value.equalsIgnoreCase("false")) {
                setGameRule(gameRule, false)
            } else {
                throw IllegalArgumentException("Was not a boolean")
            }
            Type.INTEGER -> setGameRule(gameRule, Integer.parseInt(value))
            Type.FLOAT -> setGameRule(gameRule, Float.parseFloat(value))
        }
    }

    fun getBoolean(gameRule: GameRule?): Boolean {
        return gameRules.get(gameRule).getValueAsBoolean()
    }

    fun getInteger(gameRule: GameRule?): Int {
        Preconditions.checkNotNull(gameRule, "gameRule")
        return gameRules.get(gameRule).getValueAsInteger()
    }

    fun getFloat(gameRule: GameRule?): Float {
        Preconditions.checkNotNull(gameRule, "gameRule")
        return gameRules.get(gameRule).getValueAsFloat()
    }

    fun getString(gameRule: GameRule?): String {
        Preconditions.checkNotNull(gameRule, "gameRule")
        return gameRules.get(gameRule).value.toString()
    }

    fun getGameRuleType(gameRule: GameRule?): Type {
        Preconditions.checkNotNull(gameRule, "gameRule")
        return gameRules.get(gameRule).getType()
    }

    fun hasRule(gameRule: GameRule?): Boolean {
        return gameRules.containsKey(gameRule)
    }

    val rules: Array<cn.nukkit.level.GameRule>
        get() = gameRules.keySet().toArray(EMPTY_ARRAY)

    // TODO: This needs to be moved out since there is not a separate compound tag in the LevelDB format for Game Rules.
    fun writeNBT(): CompoundTag {
        val nbt = CompoundTag()
        for (entry in gameRules.entrySet()) {
            nbt.putString(entry.getKey().getName(), entry.getValue().value.toString())
        }
        return nbt
    }

    fun readNBT(nbt: CompoundTag) {
        Preconditions.checkNotNull(nbt)
        for (key in nbt.getTags().keySet()) {
            val gameRule: Optional<GameRule> = GameRule.parseString(key)
            if (!gameRule.isPresent()) {
                continue
            }
            setGameRules(gameRule.get(), nbt.getString(key))
        }
    }

    enum class Type {
        UNKNOWN {
            @Override
            override fun write(pk: BinaryStream?, value: Value<*>?) {
            }
        },
        BOOLEAN {
            @Override
            override fun write(pk: BinaryStream, value: Value<*>) {
                pk.putBoolean(value.getValueAsBoolean())
            }
        },
        INTEGER {
            @Override
            override fun write(pk: BinaryStream, value: Value<*>) {
                pk.putUnsignedVarInt(value.getValueAsInteger())
            }
        },
        FLOAT {
            @Override
            override fun write(pk: BinaryStream, value: Value<*>) {
                pk.putLFloat(value.getValueAsFloat())
            }
        };

        abstract fun write(pk: BinaryStream?, value: Value<*>?)
    }

    class Value<T>(val type: Type, private var value: T) {
        @get:Since("1.5.0.0-PN")
        @set:Since("1.5.0.0-PN")
        var isCanBeChanged = false
        private fun setValue(value: T, type: Type) {
            if (this.type !== type) {
                throw UnsupportedOperationException("Rule not of type " + type.name().toLowerCase())
            }
            this.value = value
        }

        private val valueAsBoolean: Boolean
            private get() {
                if (type !== Type.BOOLEAN) {
                    throw UnsupportedOperationException("Rule not of type boolean")
                }
                return value as Boolean
            }
        private val valueAsInteger: Int
            private get() {
                if (type !== Type.INTEGER) {
                    throw UnsupportedOperationException("Rule not of type integer")
                }
                return value as Integer
            }
        private val valueAsFloat: Float
            private get() {
                if (type !== Type.FLOAT) {
                    throw UnsupportedOperationException("Rule not of type float")
                }
                return value as Float
            }

        @Since("1.5.0.0-PN")
        fun write(stream: BinaryStream) {
            stream.putBoolean(isCanBeChanged)
            stream.putUnsignedVarInt(type.ordinal())
            type.write(stream, this)
        }
    }

    companion object {
        val default: GameRules
            get() {
                val gameRules = GameRules()
                gameRules.gameRules.put(COMMAND_BLOCK_OUTPUT, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_DAYLIGHT_CYCLE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_ENTITY_DROPS, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_FIRE_TICK, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_IMMEDIATE_RESPAWN, Value(Type.BOOLEAN, false))
                gameRules.gameRules.put(DO_MOB_LOOT, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_MOB_SPAWNING, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_TILE_DROPS, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DO_WEATHER_CYCLE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(DROWNING_DAMAGE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(FALL_DAMAGE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(FIRE_DAMAGE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(KEEP_INVENTORY, Value(Type.BOOLEAN, false))
                gameRules.gameRules.put(MOB_GRIEFING, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(NATURAL_REGENERATION, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(PVP, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(RANDOM_TICK_SPEED, Value(Type.INTEGER, 3))
                gameRules.gameRules.put(SEND_COMMAND_FEEDBACK, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(SHOW_COORDINATES, Value(Type.BOOLEAN, false))
                gameRules.gameRules.put(TNT_EXPLODES, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(SHOW_DEATH_MESSAGE, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(EXPERIMENTAL_GAMEPLAY, Value(Type.BOOLEAN, false))
                gameRules.gameRules.put(MAX_COMMAND_CHAIN_LENGTH, Value(Type.INTEGER, 131070))
                gameRules.gameRules.put(DO_INSOMNIA, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(COMMAND_BLOCKS_ENABLED, Value(Type.BOOLEAN, true))
                gameRules.gameRules.put(FUNCTION_COMMAND_LIMIT, Value(Type.INTEGER, 20000))
                gameRules.gameRules.put(SPAWN_RADIUS, Value(Type.INTEGER, 5))
                gameRules.gameRules.put(SHOW_TAGS, Value(Type.BOOLEAN, true))
                return gameRules
            }
    }
}