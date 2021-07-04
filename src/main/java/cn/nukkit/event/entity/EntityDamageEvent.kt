package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityDamageEvent(entity: Entity, cause: DamageCause, modifiers: Map<DamageModifier?, Float?>?) : EntityEvent(), Cancellable {
    var attackCooldown = 10
    val cause: DamageCause
    private val modifiers: Map<DamageModifier, Float>
    private val originals: Map<DamageModifier, Float>

    constructor(entity: Entity?, cause: DamageCause?, damage: Float) : this(entity, cause, object : EnumMap<DamageModifier?, Float?>(DamageModifier::class.java) {
        init {
            put(DamageModifier.BASE, damage)
        }
    }) {
    }

    val originalDamage: Float
        get() = getOriginalDamage(DamageModifier.BASE)

    fun getOriginalDamage(type: DamageModifier): Float {
        return if (originals.containsKey(type)) {
            originals[type]!!
        } else 0
    }

    var damage: Float
        get() = getDamage(DamageModifier.BASE)
        set(damage) {
            setDamage(damage, DamageModifier.BASE)
        }

    fun getDamage(type: DamageModifier): Float {
        return if (modifiers.containsKey(type)) {
            modifiers[type]!!
        } else 0
    }

    fun setDamage(damage: Float, type: DamageModifier?) {
        modifiers.put(type, damage)
    }

    fun isApplicable(type: DamageModifier): Boolean {
        return modifiers.containsKey(type)
    }

    val finalDamage: Float
        get() {
            var damage = 0f
            for (d in modifiers.values()) {
                if (d != null) {
                    damage += d
                }
            }
            return damage
        }

    fun canBeReducedByArmor(): Boolean {
        when (cause) {
            DamageCause.FIRE_TICK, DamageCause.SUFFOCATION, DamageCause.DROWNING, DamageCause.HUNGER, DamageCause.FALL, DamageCause.VOID, DamageCause.MAGIC, DamageCause.SUICIDE -> return false
        }
        return true
    }

    enum class DamageModifier {
        /**
         * Raw amount of damage
         */
        BASE,

        /**
         * Damage reduction caused by wearing armor
         */
        ARMOR,

        /**
         * Additional damage caused by damager's Strength potion effect
         */
        STRENGTH,

        /**
         * Damage reduction caused by damager's Weakness potion effect
         */
        WEAKNESS,

        /**
         * Damage reduction caused by the Resistance potion effect
         */
        RESISTANCE,

        /**
         * Damage reduction caused by the Damage absorption effect
         */
        ABSORPTION,

        /**
         * Damage reduction caused by the armor enchantments worn.
         */
        ARMOR_ENCHANTMENTS
    }

    enum class DamageCause {
        /**
         * Damage caused by contact with a block such as a Cactus
         */
        CONTACT,

        /**
         * Damage caused by being attacked by another entity
         */
        ENTITY_ATTACK,

        /**
         * Damage caused by being hit by a projectile such as an Arrow
         */
        PROJECTILE,

        /**
         * Damage caused by being put in a block
         */
        SUFFOCATION,

        /**
         * Fall damage
         */
        FALL,

        /**
         * Damage caused by standing in fire
         */
        FIRE,

        /**
         * Burn damage
         */
        FIRE_TICK,

        /**
         * Damage caused by standing in lava
         */
        LAVA,

        /**
         * Damage caused by running out of air underwater
         */
        DROWNING,

        /**
         * Block explosion damage
         */
        BLOCK_EXPLOSION,

        /**
         * Entity explosion damage
         */
        ENTITY_EXPLOSION,

        /**
         * Damage caused by falling into the void
         */
        VOID,

        /**
         * Player commits suicide
         */
        SUICIDE,

        /**
         * Potion or spell damage
         */
        MAGIC,

        /**
         * Plugins
         */
        CUSTOM,

        /**
         * Damage caused by being struck by lightning
         */
        LIGHTNING,

        /**
         * Damage caused by hunger
         */
        HUNGER
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.cause = cause
        this.modifiers = EnumMap(modifiers)
        originals = ImmutableMap.copyOf(this.modifiers)
        if (!this.modifiers.containsKey(DamageModifier.BASE)) {
            throw EventException("BASE Damage modifier missing")
        }
        if (entity.hasEffect(Effect.DAMAGE_RESISTANCE)) {
            setDamage(-(getDamage(DamageModifier.BASE) * 0.20 * (entity.getEffect(Effect.DAMAGE_RESISTANCE).getAmplifier() + 1)) as Float, DamageModifier.RESISTANCE)
        }
    }
}