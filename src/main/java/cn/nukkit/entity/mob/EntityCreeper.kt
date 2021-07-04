package cn.nukkit.entity.mob

import cn.nukkit.Player

/**
 * @author Box.
 */
class EntityCreeper(chunk: FullChunk?, nbt: CompoundTag?) : EntityMob(chunk, nbt) {
    @get:Override
    override val width: Float
        get() = 0.6f

    @get:Override
    override val height: Float
        get() = 1.8f
    var isPowered: Boolean
        get() = getDataPropertyBoolean(DATA_POWERED)
        set(bolt) {
            val ev = CreeperPowerEvent(this, bolt, CreeperPowerEvent.PowerCause.LIGHTNING)
            this.getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                this.setDataProperty(ByteEntityData(DATA_POWERED, 1))
                this.namedTag.putBoolean("powered", true)
            }
        }

    fun setPowered(powered: Boolean) {
        val ev = CreeperPowerEvent(this, if (powered) CreeperPowerEvent.PowerCause.SET_ON else CreeperPowerEvent.PowerCause.SET_OFF)
        this.getServer().getPluginManager().callEvent(ev)
        if (!ev.isCancelled()) {
            this.setDataProperty(ByteEntityData(DATA_POWERED, if (powered) 1 else 0))
            this.namedTag.putBoolean("powered", powered)
        }
    }

    override fun onStruckByLightning(entity: Entity?) {
        isPowered = true
    }

    @Override
    protected override fun initEntity() {
        super.initEntity()
        if (this.namedTag.getBoolean("powered") || this.namedTag.getBoolean("IsPowered")) {
            this.dataProperties.putBoolean(DATA_POWERED, true)
        }
        this.setMaxHealth(20)
    }

    @get:Override
    override val name: String
        get() = "Creeper"

    @get:Override
    override val drops: Array<Any>
        get() = if (this.lastDamageCause is EntityDamageByEntityEvent) {
            arrayOf(Item.get(Item.GUNPOWDER, ThreadLocalRandom.current().nextInt(2) + 1))
        } else Item.EMPTY_ARRAY

    @Override
    override fun isPreventingSleep(player: Player?): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 33
            get() = Companion.field
        const val DATA_SWELL_DIRECTION = 16
        const val DATA_SWELL = 17
        const val DATA_SWELL_OLD = 18
        const val DATA_POWERED = 19
    }
}