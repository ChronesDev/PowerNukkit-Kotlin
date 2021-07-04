package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityDamageByEntityEvent : EntityDamageEvent {
    private val damager: Entity
    var knockBack: Float

    constructor(damager: Entity, entity: Entity?, cause: DamageCause?, damage: Float) : this(damager, entity, cause, damage, 0.3f) {}
    constructor(damager: Entity, entity: Entity, cause: DamageCause, modifiers: Map<DamageModifier?, Float?>?) : this(damager, entity, cause, modifiers, 0.3f) {}
    constructor(damager: Entity, entity: Entity?, cause: DamageCause?, damage: Float, knockBack: Float) : super(entity, cause, damage) {
        this.damager = damager
        this.knockBack = knockBack
        addAttackerModifiers(damager)
    }

    constructor(damager: Entity, entity: Entity, cause: DamageCause, modifiers: Map<DamageModifier?, Float?>?, knockBack: Float) : super(entity, cause, modifiers) {
        this.damager = damager
        this.knockBack = knockBack
        addAttackerModifiers(damager)
    }

    protected fun addAttackerModifiers(damager: Entity) {
        if (damager.hasEffect(Effect.STRENGTH)) {
            this.setDamage((this.getDamage(DamageModifier.BASE) * 0.3 * (damager.getEffect(Effect.STRENGTH).getAmplifier() + 1)) as Float, DamageModifier.STRENGTH)
        }
        if (damager.hasEffect(Effect.WEAKNESS)) {
            this.setDamage(-(this.getDamage(DamageModifier.BASE) * 0.2 * (damager.getEffect(Effect.WEAKNESS).getAmplifier() + 1)) as Float, DamageModifier.WEAKNESS)
        }
    }

    fun getDamager(): Entity {
        return damager
    }
}