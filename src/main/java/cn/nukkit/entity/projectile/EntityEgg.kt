package cn.nukkit.entity.projectile

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityEgg(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : EntityProjectile(chunk, nbt, shootingEntity) {
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
        protected get() = 0.03f

    @get:Override
    protected val drag: Float
        protected get() = 0.01f

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.age > 1200 || this.isCollided) {
            this.kill()
            hasUpdate = true
        }
        return hasUpdate
    }

    @Override
    protected override fun addHitEffect() {
        val particles: Int = ThreadLocalRandom.current().nextInt(10) + 5
        val egg = ItemEgg()
        for (i in 0 until particles) {
            level.addParticle(ItemBreakParticle(this, egg))
        }
    }

    companion object {
        @get:Override
        val networkId = 82
            get() = Companion.field
    }
}