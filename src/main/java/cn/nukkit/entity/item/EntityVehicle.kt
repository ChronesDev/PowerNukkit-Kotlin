package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityVehicle(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt), EntityRideable, EntityInteractable {
    var rollingAmplitude: Int
        get() = this.getDataPropertyInt(DATA_HURT_TIME)
        set(time) {
            this.setDataProperty(IntEntityData(DATA_HURT_TIME, time))
        }

    fun getRollingDirection(): Int {
        return this.getDataPropertyInt(DATA_HURT_DIRECTION)
    }

    fun setRollingDirection(direction: Int) {
        this.setDataProperty(IntEntityData(DATA_HURT_DIRECTION, direction))
    }

    // false data name (should be DATA_DAMAGE_TAKEN)
    var damage: Int
        get() = this.getDataPropertyInt(DATA_HEALTH) // false data name (should be DATA_DAMAGE_TAKEN)
        set(damage) {
            this.setDataProperty(IntEntityData(DATA_HEALTH, damage))
        }

    @get:Override
    val interactButtonText: String
        get() = "action.interact.mount"

    @Override
    fun canDoInteraction(): Boolean {
        return passengers.isEmpty()
    }

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        // The rolling amplitude
        if (rollingAmplitude > 0) {
            rollingAmplitude = rollingAmplitude - 1
        }

        // A killer task
        if (y < -16) {
            kill()
        }
        // Movement code
        updateMovement()
        return true
    }

    protected var rollingDirection = true
    protected fun performHurtAnimation(): Boolean {
        rollingAmplitude = 9
        setRollingDirection(if (rollingDirection) 1 else -1)
        rollingDirection = !rollingDirection
        return true
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        var instantKill = false
        if (source is EntityDamageByEntityEvent) {
            val damagingEntity: Entity = (source as EntityDamageByEntityEvent).getDamager()
            val byEvent = VehicleDamageByEntityEvent(this, damagingEntity, source.getFinalDamage())
            getServer().getPluginManager().callEvent(byEvent)
            if (byEvent.isCancelled()) return false
            instantKill = damagingEntity is Player && (damagingEntity as Player).isCreative()
        } else {
            val damageEvent = VehicleDamageEvent(this, source.getFinalDamage())
            getServer().getPluginManager().callEvent(damageEvent)
            if (damageEvent.isCancelled()) return false
        }
        if (instantKill || getHealth() - source.getFinalDamage() < 1) {
            if (source is EntityDamageByEntityEvent) {
                val damagingEntity: Entity = (source as EntityDamageByEntityEvent).getDamager()
                val byDestroyEvent = VehicleDestroyByEntityEvent(this, damagingEntity)
                getServer().getPluginManager().callEvent(byDestroyEvent)
                if (byDestroyEvent.isCancelled()) return false
            } else {
                val destroyEvent = VehicleDestroyEvent(this)
                getServer().getPluginManager().callEvent(destroyEvent)
                if (destroyEvent.isCancelled()) return false
            }
        }
        if (instantKill) source.setDamage(1000)
        return super.attack(source)
    }
}