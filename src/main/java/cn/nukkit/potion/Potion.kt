package cn.nukkit.potion

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Potion @JvmOverloads constructor(protected val id: Int, protected val level: Int = 1, splash: Boolean = false) : Cloneable {
    protected var splash = false
    fun getEffect(): Effect? {
        return getEffect(getId(), isSplash())
    }

    fun getId(): Int {
        return id
    }

    fun getLevel(): Int {
        return level
    }

    fun isSplash(): Boolean {
        return splash
    }

    fun setSplash(splash: Boolean): Potion {
        this.splash = splash
        return this
    }

    fun applyPotion(entity: Entity) {
        applyPotion(entity, 0.5)
    }

    fun applyPotion(entity: Entity, health: Double) {
        if (entity !is EntityLiving) {
            return
        }
        var applyEffect: Effect = getEffect(getId(), isSplash()) ?: return
        if (entity is Player) {
            if (!(entity as Player).isSurvival() && !(entity as Player).isAdventure() && applyEffect.isBad()) {
                return
            }
        }
        val event = PotionApplyEvent(this, applyEffect, entity)
        entity.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return
        }
        applyEffect = event.getApplyEffect()
        when (getId()) {
            INSTANT_HEALTH, INSTANT_HEALTH_II -> if (entity.isUndead()) entity.attack(EntityDamageEvent(entity, DamageCause.MAGIC, (health * (6 shl applyEffect.getAmplifier() + 1).toDouble()).toFloat())) else entity.heal(EntityRegainHealthEvent(entity, (health * (4 shl applyEffect.getAmplifier() + 1).toDouble()).toFloat(), EntityRegainHealthEvent.CAUSE_MAGIC))
            HARMING, HARMING_II -> if (entity.isUndead()) entity.heal(EntityRegainHealthEvent(entity, (health * (4 shl applyEffect.getAmplifier() + 1).toDouble()).toFloat(), EntityRegainHealthEvent.CAUSE_MAGIC)) else entity.attack(EntityDamageEvent(entity, DamageCause.MAGIC, (health * (6 shl applyEffect.getAmplifier() + 1).toDouble()).toFloat()))
            else -> {
                val duration = ((if (isSplash()) health else 1) * applyEffect.getDuration() as Double + 0.5) as Int
                applyEffect.setDuration(duration)
                entity.addEffect(applyEffect)
            }
        }
    }

    @Override
    fun clone(): Potion? {
        return try {
            super.clone() as Potion?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    companion object {
        const val NO_EFFECTS = 0
        const val WATER = 0
        const val MUNDANE = 1
        const val MUNDANE_II = 2
        const val THICK = 3
        const val AWKWARD = 4
        const val NIGHT_VISION = 5
        const val NIGHT_VISION_LONG = 6
        const val INVISIBLE = 7
        const val INVISIBLE_LONG = 8
        const val LEAPING = 9
        const val LEAPING_LONG = 10
        const val LEAPING_II = 11
        const val FIRE_RESISTANCE = 12
        const val FIRE_RESISTANCE_LONG = 13
        const val SPEED = 14
        const val SPEED_LONG = 15
        const val SPEED_II = 16
        const val SLOWNESS = 17
        const val SLOWNESS_LONG = 18
        const val WATER_BREATHING = 19
        const val WATER_BREATHING_LONG = 20
        const val INSTANT_HEALTH = 21
        const val INSTANT_HEALTH_II = 22
        const val HARMING = 23
        const val HARMING_II = 24
        const val POISON = 25
        const val POISON_LONG = 26
        const val POISON_II = 27
        const val REGENERATION = 28
        const val REGENERATION_LONG = 29
        const val REGENERATION_II = 30
        const val STRENGTH = 31
        const val STRENGTH_LONG = 32
        const val STRENGTH_II = 33
        const val WEAKNESS = 34
        const val WEAKNESS_LONG = 35
        const val WITHER_II = 36
        const val TURTLE_MASTER = 37
        const val TURTLE_MASTER_LONG = 38
        const val TURTLE_MASTER_II = 39
        const val SLOW_FALLING = 40
        const val SLOW_FALLING_LONG = 41

        @Since("1.4.0.0-PN")
        val SLOWNESS_LONG_II = 42

        @Since("1.4.0.0-PN")
        val SLOWNESS_IV = 43
        protected var potions: Array<Potion?>
        fun init() {
            potions = arrayOfNulls(256)
            potions[WATER] = Potion(WATER)
            potions[MUNDANE] = Potion(MUNDANE)
            potions[MUNDANE_II] = Potion(MUNDANE_II, 2)
            potions[THICK] = Potion(THICK)
            potions[AWKWARD] = Potion(AWKWARD)
            potions[NIGHT_VISION] = Potion(NIGHT_VISION)
            potions[NIGHT_VISION_LONG] = Potion(NIGHT_VISION_LONG)
            potions[INVISIBLE] = Potion(INVISIBLE)
            potions[INVISIBLE_LONG] = Potion(INVISIBLE_LONG)
            potions[LEAPING] = Potion(LEAPING)
            potions[LEAPING_LONG] = Potion(LEAPING_LONG)
            potions[LEAPING_II] = Potion(LEAPING_II, 2)
            potions[FIRE_RESISTANCE] = Potion(FIRE_RESISTANCE)
            potions[FIRE_RESISTANCE_LONG] = Potion(FIRE_RESISTANCE_LONG)
            potions[SPEED] = Potion(SPEED)
            potions[SPEED_LONG] = Potion(SPEED_LONG)
            potions[SPEED_II] = Potion(SPEED_II, 2)
            potions[SLOWNESS] = Potion(SLOWNESS)
            potions[SLOWNESS_LONG] = Potion(SLOWNESS_LONG)
            potions[WATER_BREATHING] = Potion(WATER_BREATHING)
            potions[WATER_BREATHING_LONG] = Potion(WATER_BREATHING_LONG)
            potions[INSTANT_HEALTH] = Potion(INSTANT_HEALTH)
            potions[INSTANT_HEALTH_II] = Potion(INSTANT_HEALTH_II, 2)
            potions[HARMING] = Potion(HARMING)
            potions[HARMING_II] = Potion(HARMING_II, 2)
            potions[POISON] = Potion(POISON)
            potions[POISON_LONG] = Potion(POISON_LONG)
            potions[POISON_II] = Potion(POISON_II, 2)
            potions[REGENERATION] = Potion(REGENERATION)
            potions[REGENERATION_LONG] = Potion(REGENERATION_LONG)
            potions[REGENERATION_II] = Potion(REGENERATION_II, 2)
            potions[STRENGTH] = Potion(STRENGTH)
            potions[STRENGTH_LONG] = Potion(STRENGTH_LONG)
            potions[STRENGTH_II] = Potion(STRENGTH_II, 2)
            potions[WEAKNESS] = Potion(WEAKNESS)
            potions[WEAKNESS_LONG] = Potion(WEAKNESS_LONG)
            potions[WITHER_II] = Potion(WITHER_II, 2)
            potions[TURTLE_MASTER] = Potion(TURTLE_MASTER)
            potions[TURTLE_MASTER_LONG] = Potion(TURTLE_MASTER_LONG)
            potions[TURTLE_MASTER_II] = Potion(TURTLE_MASTER_II, 2)
            potions[SLOW_FALLING] = Potion(SLOW_FALLING)
            potions[SLOW_FALLING_LONG] = Potion(SLOW_FALLING_LONG)
            potions[SLOWNESS_LONG_II] = Potion(SLOWNESS_LONG_II, 2)
            potions[SLOWNESS_IV] = Potion(SLOWNESS, 4)
        }

        fun getPotion(id: Int): Potion? {
            return if (id >= 0 && id < potions.size && potions[id] != null) {
                potions[id]!!.clone()
            } else {
                throw ServerException("Effect id: $id not found")
            }
        }

        fun getPotionByName(name: String): Potion? {
            return try {
                val id: Byte = Potion::class.java.getField(name.toUpperCase()).getByte(null)
                getPotion(id.toInt())
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        fun getEffect(potionType: Int, isSplash: Boolean): Effect? {
            val effect: Effect
            effect = when (potionType) {
                NIGHT_VISION, NIGHT_VISION_LONG -> Effect.getEffect(Effect.NIGHT_VISION)
                INVISIBLE, INVISIBLE_LONG -> Effect.getEffect(Effect.INVISIBILITY)
                LEAPING, LEAPING_LONG, LEAPING_II -> Effect.getEffect(Effect.JUMP)
                FIRE_RESISTANCE, FIRE_RESISTANCE_LONG -> Effect.getEffect(Effect.FIRE_RESISTANCE)
                SPEED, SPEED_LONG, SPEED_II -> Effect.getEffect(Effect.SPEED)
                SLOWNESS, SLOWNESS_LONG, SLOWNESS_IV -> Effect.getEffect(Effect.SLOWNESS)
                WATER_BREATHING, WATER_BREATHING_LONG -> Effect.getEffect(Effect.WATER_BREATHING)
                INSTANT_HEALTH, INSTANT_HEALTH_II -> return Effect.getEffect(Effect.HEALING)
                HARMING, HARMING_II -> return Effect.getEffect(Effect.HARMING)
                POISON, POISON_LONG, POISON_II -> Effect.getEffect(Effect.POISON)
                REGENERATION, REGENERATION_LONG, REGENERATION_II -> Effect.getEffect(Effect.REGENERATION)
                STRENGTH, STRENGTH_LONG, STRENGTH_II -> Effect.getEffect(Effect.STRENGTH)
                WEAKNESS, WEAKNESS_LONG -> Effect.getEffect(Effect.WEAKNESS)
                WITHER_II -> Effect.getEffect(Effect.WITHER)
                else -> return null
            }
            if (getLevel(potionType) > 1) {
                effect!!.setAmplifier(1)
            }
            if (!isInstant(potionType)) {
                effect!!.setDuration(20 * getApplySeconds(potionType, isSplash))
            }
            return effect
        }

        fun getLevel(potionType: Int): Int {
            return when (potionType) {
                SLOWNESS_IV -> 4
                MUNDANE_II, LEAPING_II, SPEED_II, INSTANT_HEALTH_II, HARMING_II, POISON_II, REGENERATION_II, STRENGTH_II, WITHER_II, TURTLE_MASTER_II -> 2
                else -> 1
            }
        }

        fun isInstant(potionType: Int): Boolean {
            return when (potionType) {
                INSTANT_HEALTH, INSTANT_HEALTH_II, HARMING, HARMING_II -> true
                else -> false
            }
        }

        fun getApplySeconds(potionType: Int, isSplash: Boolean): Int {
            return if (isSplash) {
                when (potionType) {
                    NIGHT_VISION, STRENGTH, WATER_BREATHING, SPEED, FIRE_RESISTANCE, LEAPING, INVISIBLE -> 135
                    NIGHT_VISION_LONG, STRENGTH_LONG, WATER_BREATHING_LONG, SPEED_LONG, FIRE_RESISTANCE_LONG, LEAPING_LONG, INVISIBLE_LONG -> 360
                    LEAPING_II, WEAKNESS, STRENGTH_II, SLOWNESS, SPEED_II -> 67
                    SLOWNESS_LONG, WEAKNESS_LONG -> 180
                    POISON, REGENERATION -> 33
                    POISON_LONG, REGENERATION_LONG -> 90
                    POISON_II, REGENERATION_II -> 16
                    WITHER_II -> 30
                    SLOWNESS_IV -> 15
                    else -> 0
                }
            } else {
                when (potionType) {
                    NIGHT_VISION, STRENGTH, WATER_BREATHING, SPEED, FIRE_RESISTANCE, LEAPING, INVISIBLE -> 180
                    NIGHT_VISION_LONG, STRENGTH_LONG, WATER_BREATHING_LONG, SPEED_II, SPEED_LONG, FIRE_RESISTANCE_LONG, LEAPING_LONG, INVISIBLE_LONG -> 480
                    LEAPING_II, WEAKNESS, STRENGTH_II, SLOWNESS -> 90
                    SLOWNESS_LONG, WEAKNESS_LONG -> 240
                    POISON, REGENERATION -> 45
                    POISON_LONG, REGENERATION_LONG -> 120
                    POISON_II, REGENERATION_II -> 22
                    WITHER_II -> 30
                    SLOWNESS_IV -> 20
                    else -> 0
                }
            }
        }
    }

    init {
        this.splash = splash
    }
}