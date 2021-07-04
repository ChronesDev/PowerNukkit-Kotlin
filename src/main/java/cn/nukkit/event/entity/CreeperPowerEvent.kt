package cn.nukkit.event.entity

import cn.nukkit.entity.mob.EntityCreeper

/**
 * @author MagicDroidX (Nukkit Project)
 */
class CreeperPowerEvent(creeper: EntityCreeper?, cause: PowerCause?) : EntityEvent(), Cancellable {
    /**
     * Gets the cause of the creeper being (un)powered.
     *
     * @return A PowerCause value detailing the cause of change in power.
     */
    val cause: PowerCause?
    private var bolt: EntityLightningStrike? = null

    constructor(creeper: EntityCreeper?, bolt: EntityLightningStrike?, cause: PowerCause?) : this(creeper, cause) {
        this.bolt = bolt
    }

    @Override
    override fun getEntity(): EntityCreeper? {
        return super.getEntity() as EntityCreeper?
    }

    /**
     * Gets the lightning bolt which is striking the Creeper.
     *
     * @return The Entity for the lightning bolt which is striking the Creeper
     */
    val lightning: EntityLightningStrike?
        get() = bolt

    /**
     * An enum to specify the cause of the change in power
     */
    enum class PowerCause {
        /**
         * Power change caused by a lightning bolt
         *
         *
         * Powered state: true
         */
        LIGHTNING,

        /**
         * Power change caused by something else (probably a plugin)
         *
         *
         * Powered state: true
         */
        SET_ON,

        /**
         * Power change caused by something else (probably a plugin)
         *
         *
         * Powered state: false
         */
        SET_OFF
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.entity = creeper
        this.cause = cause
    }
}