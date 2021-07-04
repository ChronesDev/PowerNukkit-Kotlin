package cn.nukkit.entity.item

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 */
class EntityExpBottle(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : EntityProjectile(chunk, nbt, shootingEntity) {
    @get:Override
    val width: Float
        get() = 0.25f

    @get:Override
    val length: Float
        get() = 0.25f

    @get:Override
    val height: Float
        get() = 0.25f

    @get:Override
    protected val gravity: Float
        protected get() = 0.1f

    @get:Override
    protected val drag: Float
        protected get() = 0.01f

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.age > 1200) {
            this.kill()
            hasUpdate = true
        }
        if (this.isCollided) {
            this.kill()
            dropXp()
            hasUpdate = true
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    @Override
    fun onCollideWithEntity(entity: Entity?) {
        this.kill()
        dropXp()
    }

    fun dropXp() {
        val particle2: Particle = SpellParticle(this, 0x00385dc6)
        this.getLevel().addParticle(particle2)
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_GLASS)
        this.getLevel().dropExpOrb(this, ThreadLocalRandom.current().nextInt(3, 12))
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    protected fun addHitEffect() {
        this.getLevel().addSound(this, Sound.RANDOM_GLASS)
    }

    companion object {
        @get:Override
        val networkId = 68
            get() = Companion.field
    }
}