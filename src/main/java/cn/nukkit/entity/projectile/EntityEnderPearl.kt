package cn.nukkit.entity.projectile

import cn.nukkit.Player

class EntityEnderPearl(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : EntityProjectile(chunk, nbt, shootingEntity) {
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
        if (this.isCollided && this.shootingEntity is Player) {
            var portal = false
            for (collided in this.getCollisionBlocks()) {
                if (collided.getId() === Block.NETHER_PORTAL) {
                    portal = true
                }
            }
            if (!portal) {
                teleport()
            }
        }
        if (this.age > 1200 || this.isCollided) {
            this.kill()
            hasUpdate = true
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    @Override
    override fun onCollideWithEntity(entity: Entity) {
        if (this.shootingEntity is Player) {
            teleport()
        }
        super.onCollideWithEntity(entity)
    }

    private fun teleport() {
        if (!this.level.equals(this.shootingEntity.getLevel())) {
            return
        }
        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_PORTAL)
        this.shootingEntity.teleport(Vector3(NukkitMath.floorDouble(this.x) + 0.5, this.y, NukkitMath.floorDouble(this.z) + 0.5), TeleportCause.ENDER_PEARL)
        if ((this.shootingEntity as Player).getGamemode() and 0x01 === 0) {
            this.shootingEntity.attack(EntityDamageByEntityEvent(this, shootingEntity, EntityDamageEvent.DamageCause.PROJECTILE, 5f, 0f))
        }
        this.level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_ENDERMAN_TELEPORT)
        this.level.addLevelEvent(this.shootingEntity.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_PORTAL)
    }

    companion object {
        @get:Override
        val networkId = 87
            get() = Companion.field
    }
}