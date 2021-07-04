package cn.nukkit.potion

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Effect(protected val id: Int, protected val name: String?, r: Int, g: Int, b: Int, protected val bad: Boolean) : Cloneable {
    protected var duration = 0
    protected var amplifier = 0
    protected var color = 0
    protected var show = true
    protected var ambient = false

    constructor(id: Int, name: String?, r: Int, g: Int, b: Int) : this(id, name, r, g, b, false) {}

    fun getName(): String? {
        return name
    }

    fun getId(): Int {
        return id
    }

    fun setDuration(ticks: Int): Effect {
        duration = ticks
        return this
    }

    fun getDuration(): Int {
        return duration
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Cloudburst Nukkit always returns false for VILLAGE_HERO, we made the registration register the false value as default instead of this brute-force.")
    fun isVisible(): Boolean {
        return show
    }

    fun setVisible(visible: Boolean): Effect {
        show = visible
        return this
    }

    fun getAmplifier(): Int {
        return amplifier
    }

    fun setAmplifier(amplifier: Int): Effect {
        this.amplifier = amplifier
        return this
    }

    fun isAmbient(): Boolean {
        return ambient
    }

    fun setAmbient(ambient: Boolean): Effect {
        this.ambient = ambient
        return this
    }

    fun isBad(): Boolean {
        return bad
    }

    fun canTick(): Boolean {
        var interval: Int
        when (id) {
            POISON, FATAL_POISON -> {
                return if ((25 shr amplifier).also { interval = it } > 0) {
                    duration % interval == 0
                } else true
            }
            WITHER -> {
                return if ((50 shr amplifier).also { interval = it } > 0) {
                    duration % interval == 0
                } else true
            }
            REGENERATION -> {
                return if ((40 shr amplifier).also { interval = it } > 0) {
                    duration % interval == 0
                } else true
            }
        }
        return false
    }

    fun applyEffect(entity: Entity) {
        when (id) {
            POISON, FATAL_POISON -> if (entity.getHealth() > 1 || id == FATAL_POISON) {
                entity.attack(EntityDamageEvent(entity, DamageCause.MAGIC, 1))
            }
            WITHER -> entity.attack(EntityDamageEvent(entity, DamageCause.MAGIC, 1))
            REGENERATION -> if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC))
            }
        }
    }

    fun getColor(): IntArray {
        return intArrayOf(color shr 16, color shr 8 and 0xff, color and 0xff)
    }

    fun setColor(r: Int, g: Int, b: Int) {
        color = (r and 0xff shl 16) + (g and 0xff shl 8) + (b and 0xff)
    }

    fun add(entity: Entity) {
        val oldEffect: Effect = entity.getEffect(getId())
        if (oldEffect != null && (Math.abs(getAmplifier()) < Math.abs(oldEffect.getAmplifier()) ||
                        Math.abs(getAmplifier()) === Math.abs(oldEffect.getAmplifier())
                        && getDuration() < oldEffect.getDuration())) {
            return
        }
        if (entity is Player) {
            val player: Player = entity as Player
            val pk = MobEffectPacket()
            pk.eid = entity.getId()
            pk.effectId = getId()
            pk.amplifier = getAmplifier()
            pk.particles = isVisible()
            pk.duration = getDuration()
            if (oldEffect != null) {
                pk.eventId = MobEffectPacket.EVENT_MODIFY
            } else {
                pk.eventId = MobEffectPacket.EVENT_ADD
            }
            player.dataPacket(pk)
            if (id == SPEED && (oldEffect == null || oldEffect.amplifier != amplifier)) {
                if (oldEffect != null) {
                    player.setMovementSpeed(player.getMovementSpeed() / (1 + 0.2f * (oldEffect.amplifier + 1)), false)
                }
                player.setMovementSpeed(player.getMovementSpeed() * (1 + 0.2f * (amplifier + 1)))
            }
            if (id == SLOWNESS && (oldEffect == null || oldEffect.amplifier != amplifier)) {
                if (oldEffect != null) {
                    player.setMovementSpeed(player.getMovementSpeed() / (1 - 0.15f * (oldEffect.amplifier + 1)), false)
                }
                player.setMovementSpeed(player.getMovementSpeed() * (1 - 0.15f * (amplifier + 1)))
            }
        }
        if (id == INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, true)
            entity.setNameTagVisible(false)
        }
        if (id == ABSORPTION) {
            val add = (amplifier + 1) * 4
            if (add > entity.getAbsorption()) entity.setAbsorption(add)
        }
    }

    fun remove(entity: Entity) {
        if (entity is Player) {
            val pk = MobEffectPacket()
            pk.eid = entity.getId()
            pk.effectId = getId()
            pk.eventId = MobEffectPacket.EVENT_REMOVE
            (entity as Player).dataPacket(pk)
            if (id == SPEED) {
                (entity as Player).setMovementSpeed((entity as Player).getMovementSpeed() / (1 + 0.2f * (amplifier + 1)))
            }
            if (id == SLOWNESS) {
                (entity as Player).setMovementSpeed((entity as Player).getMovementSpeed() / (1 - 0.15f * (amplifier + 1)))
            }
            if (id == HEALTH_BOOST) {
                val max: Float = entity.getMaxHealth()
                val health: Float = Math.min(entity.getHealth(), max)
                entity.setHealth(health)
            }
        }
        if (id == INVISIBILITY) {
            entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_INVISIBLE, false)
            entity.setNameTagVisible(true)
        }
        if (id == ABSORPTION) {
            entity.setAbsorption(0)
        }
    }

    @Override
    fun clone(): Effect? {
        return try {
            super.clone() as Effect?
        } catch (e: CloneNotSupportedException) {
            null
        }
    }

    companion object {
        const val SPEED = 1
        const val SLOWNESS = 2
        const val HASTE = 3
        const val SWIFTNESS = 3
        const val FATIGUE = 4
        const val MINING_FATIGUE = 4
        const val STRENGTH = 5

        @Since("1.4.0.0-PN")
        val INSTANT_HEALTH = 6

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Was renamed to INSTANT_HEALTH in game, can be removed anytime by Cloudburst Nukkit", replaceWith = "INSTANT_HEALTH")
        val HEALING = INSTANT_HEALTH

        @Since("1.4.0.0-PN")
        val INSTANT_DAMAGE = 7

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Was renamed to INSTANT_DAMAGE in game, can be removed anytime by Cloudburst Nukkit", replaceWith = "INSTANT_DAMAGE")
        val HARMING = INSTANT_DAMAGE

        @Since("1.4.0.0-PN")
        val JUMP_BOOST = 8

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Was renamed to JUMP_BOOST in game, can be removed anytime by Cloudburst Nukkit", replaceWith = "JUMP_BOOST")
        val JUMP = JUMP_BOOST
        const val NAUSEA = 9

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Was renamed to NAUSEA in game, can be removed anytime by Cloudburst Nukkit", replaceWith = "NAUSEA")
        val CONFUSION = 9
        const val REGENERATION = 10

        @Since("1.4.0.0-PN")
        val RESISTANCE = 11

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Was renamed to JUMP_BOOST in game, can be removed anytime by Cloudburst Nukkit", replaceWith = "JUMP_BOOST")
        val DAMAGE_RESISTANCE = RESISTANCE
        const val FIRE_RESISTANCE = 12
        const val WATER_BREATHING = 13
        const val INVISIBILITY = 14
        const val BLINDNESS = 15
        const val NIGHT_VISION = 16
        const val HUNGER = 17
        const val WEAKNESS = 18
        const val POISON = 19
        const val WITHER = 20
        const val HEALTH_BOOST = 21
        const val ABSORPTION = 22
        const val SATURATION = 23
        const val LEVITATION = 24
        const val FATAL_POISON = 25
        const val CONDUIT_POWER = 26

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit and removed by Cloudburst", since = "TBD", reason = "Typo", replaceWith = "CONDUIT_POWER")
        @PowerNukkitOnly("Was removed from Cloudburst Nukkit, kept on PowerNukkit for backward compatibility")
        @Deprecated("Typo. Use {@link #CONDUIT_POWER} instead.")
        val COUNDIT_POWER = CONDUIT_POWER
        const val SLOW_FALLING = 27

        @Since("1.4.0.0-PN")
        val BAD_OMEN = 28

        @Since("1.4.0.0-PN")
        val VILLAGE_HERO = 29
        protected var effects: Array<Effect?>
        fun init() {
            effects = arrayOfNulls(256)
            effects[SPEED] = Effect(SPEED, "%potion.moveSpeed", 124, 175, 198)
            effects[SLOWNESS] = Effect(SLOWNESS, "%potion.moveSlowdown", 90, 108, 129, true)
            effects[SWIFTNESS] = Effect(SWIFTNESS, "%potion.digSpeed", 217, 192, 67)
            effects[FATIGUE] = Effect(FATIGUE, "%potion.digSlowDown", 74, 66, 23, true)
            effects[STRENGTH] = Effect(STRENGTH, "%potion.damageBoost", 147, 36, 35)
            effects[INSTANT_HEALTH] = InstantEffect(INSTANT_HEALTH, "%potion.heal", 248, 36, 35)
            effects[INSTANT_DAMAGE] = InstantEffect(INSTANT_DAMAGE, "%potion.harm", 67, 10, 9, true)
            effects[JUMP_BOOST] = Effect(JUMP_BOOST, "%potion.jump", 34, 255, 76)
            effects[NAUSEA] = Effect(NAUSEA, "%potion.confusion", 85, 29, 74, true)
            effects[REGENERATION] = Effect(REGENERATION, "%potion.regeneration", 205, 92, 171)
            effects[RESISTANCE] = Effect(RESISTANCE, "%potion.resistance", 153, 69, 58)
            effects[FIRE_RESISTANCE] = Effect(FIRE_RESISTANCE, "%potion.fireResistance", 228, 154, 58)
            effects[WATER_BREATHING] = Effect(WATER_BREATHING, "%potion.waterBreathing", 46, 82, 153)
            effects[INVISIBILITY] = Effect(INVISIBILITY, "%potion.invisibility", 127, 131, 146)
            effects[BLINDNESS] = Effect(BLINDNESS, "%potion.blindness", 191, 192, 192)
            effects[NIGHT_VISION] = Effect(NIGHT_VISION, "%potion.nightVision", 0, 0, 139)
            effects[HUNGER] = Effect(HUNGER, "%potion.hunger", 46, 139, 87)
            effects[WEAKNESS] = Effect(WEAKNESS, "%potion.weakness", 72, 77, 72, true)
            effects[POISON] = Effect(POISON, "%potion.poison", 78, 147, 49, true)
            effects[WITHER] = Effect(WITHER, "%potion.wither", 53, 42, 39, true)
            effects[HEALTH_BOOST] = Effect(HEALTH_BOOST, "%potion.healthBoost", 248, 125, 35)
            effects[ABSORPTION] = Effect(ABSORPTION, "%potion.absorption", 36, 107, 251)
            effects[SATURATION] = Effect(SATURATION, "%potion.saturation", 255, 0, 255)
            effects[LEVITATION] = Effect(LEVITATION, "%potion.levitation", 206, 255, 255, true)
            effects[FATAL_POISON] = Effect(FATAL_POISON, "%potion.poison", 78, 147, 49, true)
            effects[CONDUIT_POWER] = Effect(CONDUIT_POWER, "%potion.conduitPower", 29, 194, 209)
            effects[SLOW_FALLING] = Effect(SLOW_FALLING, "%potion.slowFalling", 206, 255, 255)
            effects[BAD_OMEN] = Effect(BAD_OMEN, "%effect.badOmen", 11, 97, 56, true)
            effects[VILLAGE_HERO] = Effect(VILLAGE_HERO, "%effect.villageHero", 68, 255, 68).setVisible(false)
        }

        fun getEffect(id: Int): Effect? {
            return if (id >= 0 && id < effects.size && effects[id] != null) {
                effects[id]!!.clone()
            } else {
                throw ServerException("Effect id: $id not found")
            }
        }

        fun getEffectByName(name: String): Effect? {
            var name = name
            name = name.trim().replace(' ', '_').replace("minecraft:", "")
            return try {
                val id: Int = Effect::class.java.getField(name.toUpperCase()).getInt(null)
                getEffect(id)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    init {
        setColor(r, g, b)
    }
}