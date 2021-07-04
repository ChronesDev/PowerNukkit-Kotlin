package cn.nukkit.entity.item

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX
 */
class EntityPrimedTNT(chunk: FullChunk?, nbt: CompoundTag?, source: Entity?) : Entity(chunk, nbt), EntityExplosive {
    @get:Override
    val width: Float
        get() = 0.98f

    @get:Override
    val length: Float
        get() = 0.98f

    @get:Override
    val height: Float
        get() = 0.98f

    @get:Override
    protected val gravity: Float
        protected get() = 0.04f

    @get:Override
    protected val drag: Float
        protected get() = 0.02f

    @get:Override
    protected val baseOffset: Float
        protected get() = 0.49f

    @Override
    fun canCollide(): Boolean {
        return false
    }

    protected var fuse = 0
    protected var source: Entity?

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return source.getCause() === DamageCause.VOID && super.attack(source)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    protected fun initEntity() {
        super.initEntity()
        fuse = if (namedTag.contains("Fuse")) {
            namedTag.getByte("Fuse")
        } else {
            80
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, true)
        this.setDataProperty(IntEntityData(DATA_FUSE_LENGTH, fuse))
        this.getLevel().addSound(this, Sound.RANDOM_FUSE)
    }

    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    fun saveNBT() {
        super.saveNBT()
        namedTag.putByte("Fuse", fuse)
    }

    fun onUpdate(currentTick: Int): Boolean {
        if (closed) {
            return false
        }
        this.timing.startTiming()
        val tickDiff: Int = currentTick - lastUpdate
        if (tickDiff <= 0 && !justCreated) {
            return true
        }
        if (fuse % 5 == 0) {
            this.setDataProperty(IntEntityData(DATA_FUSE_LENGTH, fuse))
        }
        lastUpdate = currentTick
        val hasUpdate: Boolean = entityBaseTick(tickDiff)
        if (isAlive()) {
            motionY -= gravity
            move(motionX, motionY, motionZ)
            val friction = 1 - drag
            motionX *= friction
            motionY *= friction
            motionZ *= friction
            updateMovement()
            if (onGround) {
                motionY *= -0.5
                motionX *= 0.7
                motionZ *= 0.7
            }
            fuse -= tickDiff
            if (fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) explode()
                kill()
            }
        }
        this.timing.stopTiming()
        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001
    }

    fun explode() {
        val event = EntityExplosionPrimeEvent(this, 4)
        server.getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return
        }
        val explosion = Explosion(this, event.getForce(), this)
        explosion.setFireChance(event.getFireChance())
        if (event.isBlockBreaking()) {
            explosion.explodeA()
        }
        explosion.explodeB()
    }

    fun getSource(): Entity? {
        return source
    }

    companion object {
        @get:Override
        val networkId = 65
            get() = Companion.field
    }

    init {
        this.source = source
    }
}