package cn.nukkit.entity.projectile

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityArrow(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?, critical: Boolean) : EntityProjectile(chunk, nbt, shootingEntity) {
    var pickupMode = 0

    @get:Override
    val width: Float
        get() = 0.5f

    @get:Override
    val length: Float
        get() = 0.5f

    @get:Override
    val height: Float
        get() = 0.5f

    @get:Override
    val gravity: Float
        get() = 0.05f

    @get:Override
    val drag: Float
        get() = 0.01f

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    protected override fun updateMotion() {
        if (!isInsideOfWater()) {
            super.updateMotion()
            return
        }
        val drag = 1 - drag * 20
        motionY -= gravity * 2
        if (motionY < 0) {
            motionY *= drag / 1.5
        }
        motionX *= drag
        motionZ *= drag
    }

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}
    constructor(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : this(chunk, nbt, shootingEntity, false) {}

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.damage = if (namedTag.contains("damage")) namedTag.getDouble("damage") else 2
        pickupMode = if (namedTag.contains("pickup")) namedTag.getByte("pickup") else PICKUP_ANY
    }

    fun setCritical() {
        isCritical = true
    }

    var isCritical: Boolean
        get() = this.getDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_CRITICAL, value)
        }

    @get:Override
    override val resultDamage: Int
        get() {
            var base: Int = super.getResultDamage()
            if (isCritical) {
                base += ThreadLocalRandom.current().nextInt(base / 2 + 2)
            }
            return base
        }

    @get:Override
    protected override val baseDamage: Double
        protected get() = 2

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.onGround || this.hadCollision) {
            isCritical = false
        }
        if (this.age > 1200) {
            this.close()
            hasUpdate = true
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    @Override
    fun canBeMovedByCurrents(): Boolean {
        return !hadCollision
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    protected override fun afterCollisionWithEntity(entity: Entity?) {
        if (hadCollision) {
            close()
        } else {
            setMotion(getMotion().divide(-4))
        }
    }

    @Override
    protected override fun addHitEffect() {
        this.level.addSound(this, Sound.RANDOM_BOWHIT)
        val packet = EntityEventPacket()
        packet.eid = getId()
        packet.event = EntityEventPacket.ARROW_SHAKE
        packet.data = 7 // TODO Magic value. I have no idea why we have to set it to 7 here...
        Server.broadcastPacket(this.hasSpawned.values(), packet)
        onGround = true
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("pickup", pickupMode)
    }

    companion object {
        @get:Override
        val networkId = 80
            get() = Companion.field
        const val DATA_SOURCE_ID = 17
        const val PICKUP_NONE = 0
        const val PICKUP_ANY = 1
        const val PICKUP_CREATIVE = 2
    }

    init {
        closeOnCollide = false
        isCritical = critical
    }
}