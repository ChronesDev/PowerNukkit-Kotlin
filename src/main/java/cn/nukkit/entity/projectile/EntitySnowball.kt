package cn.nukkit.entity.projectile

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntitySnowball(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : EntityProjectile(chunk, nbt, shootingEntity) {
    companion object {
        @get:Override
        val networkId = 81
            get() = Companion.field
        private val particleCounts = ByteArray(24)
        private var particleIndex = 0
        private fun nextParticleCount(): Int {
            var index = particleIndex++
            if (index >= particleCounts.size) {
                index = 0
                particleIndex = index
            }
            return particleCounts[index].toInt()
        }

        init {
            for (i in particleCounts.indices) {
                particleCounts[i] = (ThreadLocalRandom.current().nextInt(10) + 5) as Byte
            }
        }
    }

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
        this.timing.startTiming()
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.age > 1200 || this.isCollided) {
            this.kill()
            hasUpdate = true
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    @PowerNukkitOnly
    @Override
    override fun getResultDamage(@Nullable entity: Entity?): Int {
        return if (entity is EntityBlaze) 3 else super.getResultDamage(entity)
    }

    @Override
    protected override fun addHitEffect() {
        val particles = nextParticleCount()
        val particlePackets: Array<DataPacket> = GenericParticle(this, Particle.TYPE_SNOWBALL_POOF).encode()
        val length = particlePackets.size
        val allPackets: Array<DataPacket> = Arrays.copyOf(particlePackets, length * particles)
        for (i in length until allPackets.size) {
            allPackets[i] = particlePackets[i % length]
        }
        val chunkX = x as Int shr 4
        val chunkZ = z as Int shr 4
        val level: Level = this.level
        level.getServer().batchPackets(level.getChunkPlayers(chunkX, chunkZ).values().toArray(Player.EMPTY_ARRAY), allPackets)
    }
}