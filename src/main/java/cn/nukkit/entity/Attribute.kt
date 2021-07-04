package cn.nukkit.entity

/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
import cn.nukkit.api.PowerNukkitOnly

@ToString
class Attribute private constructor(val id: Int, var name: String, var minValue: Float, var maxValue: Float, var defaultValue: Float, var isSyncable: Boolean) : Cloneable {
    var value: Float
        protected set

    fun setMinValue(minValue: Float): Attribute {
        if (minValue > maxValue) {
            throw IllegalArgumentException("Value $minValue is bigger than the maxValue!")
        }
        this.minValue = minValue
        return this
    }

    fun setMaxValue(maxValue: Float): Attribute {
        if (maxValue < minValue) {
            throw IllegalArgumentException("Value $maxValue is bigger than the minValue!")
        }
        this.maxValue = maxValue
        return this
    }

    fun setDefaultValue(defaultValue: Float): Attribute {
        if (defaultValue > maxValue || defaultValue < minValue) {
            throw IllegalArgumentException("Value $defaultValue exceeds the range!")
        }
        this.defaultValue = defaultValue
        return this
    }

    fun setValue(value: Float): Attribute {
        return setValue(value, true)
    }

    fun setValue(value: Float, fit: Boolean): Attribute {
        var value = value
        if (value > maxValue || value < minValue) {
            if (!fit) {
                throw IllegalArgumentException("Value $value exceeds the range!")
            }
            value = Math.min(Math.max(value, minValue), maxValue)
        }
        this.value = value
        return this
    }

    @Override
    fun clone(): Attribute? {
        return try {
            super.clone() as Attribute?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Attribute>(0)
        const val ABSORPTION = 0
        const val SATURATION = 1
        const val EXHAUSTION = 2
        const val KNOCKBACK_RESISTANCE = 3
        const val MAX_HEALTH = 4
        const val MOVEMENT_SPEED = 5
        const val FOLLOW_RANGE = 6
        const val MAX_HUNGER = 7
        const val FOOD = 7
        const val ATTACK_DAMAGE = 8
        const val EXPERIENCE_LEVEL = 9
        const val EXPERIENCE = 10
        const val LUCK = 11
        protected var attributes: Map<Integer, Attribute> = HashMap()
        fun init() {
            addAttribute(ABSORPTION, "minecraft:absorption", 0.00f, 340282346638528859811704183484516925440.00f, 0.00f)
            addAttribute(SATURATION, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f)
            addAttribute(EXHAUSTION, "minecraft:player.exhaustion", 0.00f, 5.00f, 0.41f)
            addAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0.00f, 1.00f, 0.00f)
            addAttribute(MAX_HEALTH, "minecraft:health", 0.00f, 20.00f, 20.00f)
            addAttribute(MOVEMENT_SPEED, "minecraft:movement", 0.00f, 340282346638528859811704183484516925440.00f, 0.10f)
            addAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0.00f, 2048.00f, 16.00f, false)
            addAttribute(MAX_HUNGER, "minecraft:player.hunger", 0.00f, 20.00f, 20.00f)
            addAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0.00f, 340282346638528859811704183484516925440.00f, 1.00f, false)
            addAttribute(EXPERIENCE_LEVEL, "minecraft:player.level", 0.00f, 24791.00f, 0.00f)
            addAttribute(EXPERIENCE, "minecraft:player.experience", 0.00f, 1.00f, 0.00f)
            addAttribute(LUCK, "minecraft:luck", -1024f, 1024f, 0f)
        }

        fun addAttribute(id: Int, name: String, minValue: Float, maxValue: Float, defaultValue: Float): Attribute {
            return addAttribute(id, name, minValue, maxValue, defaultValue, true)
        }

        fun addAttribute(id: Int, name: String, minValue: Float, maxValue: Float, defaultValue: Float, shouldSend: Boolean): Attribute {
            if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
                throw IllegalArgumentException("Invalid ranges: min value: $minValue, max value: $maxValue, defaultValue: $defaultValue")
            }
            return attributes.put(id, Attribute(id, name, minValue, maxValue, defaultValue, shouldSend))
        }

        fun getAttribute(id: Int): Attribute? {
            if (attributes.containsKey(id)) {
                return attributes[id]!!.clone()
            }
            throw ServerException("Attribute id: $id not found")
        }

        /**
         * @param name name
         * @return null|Attribute
         */
        fun getAttributeByName(name: String?): Attribute? {
            for (a in attributes.values()) {
                if (Objects.equals(a.name, name)) {
                    return a.clone()
                }
            }
            return null
        }
    }

    init {
        value = defaultValue
    }
}