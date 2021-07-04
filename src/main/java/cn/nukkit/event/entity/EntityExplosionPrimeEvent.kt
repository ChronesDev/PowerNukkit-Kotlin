package cn.nukkit.event.entity

import cn.nukkit.api.PowerNukkitOnly

/**
 * @since 15-10-27
 */
class EntityExplosionPrimeEvent(entity: Entity?, force: Double) : EntityEvent(), Cancellable {
    var force: Double
    var isBlockBreaking: Boolean

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var fireChance = 0.0

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isIncendiary: Boolean
        get() = fireChance > 0
        set(incendiary) {
            if (!incendiary) {
                fireChance = 0.0
            } else if (fireChance <= 0) {
                fireChance = 1.0 / 3.0
            }
        }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        entity = entity
        this.force = force
        isBlockBreaking = true
    }
}