package cn.nukkit.event.entity

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class EntityDamageBlockedEvent @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(entity: Entity?, damage: EntityDamageEvent, knockBack: Boolean, animation: Boolean) : EntityEvent(), Cancellable {
    private val damage: EntityDamageEvent

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val knockBackAttacker: Boolean

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val animation: Boolean

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val cause: cn.nukkit.event.entity.EntityDamageEvent.DamageCause
        get() = damage.getCause()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val attacker: Entity
        get() = damage.getEntity()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getDamage(): EntityDamageEvent {
        return damage
    }

    companion object {
        @get:Since("1.4.0.0-PN")
        @get:PowerNukkitOnly
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.damage = damage
        knockBackAttacker = knockBack
        this.animation = animation
    }
}