package cn.nukkit.entity.projectile

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityProjectile(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : Entity(chunk, nbt) {
    var shootingEntity: Entity? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var hasAge = true
    protected fun getDamage(): Double {
        return if (namedTag.contains("damage")) namedTag.getDouble("damage") else baseDamage
    }

    protected val baseDamage: Double
        protected get() = 0
    var hadCollision = false
    var closeOnCollide = true
    protected var damage = 0.0

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}

    @PowerNukkitOnly("Allows to modify the damage based on the entity being damaged")
    @Since("1.4.0.0-PN")
    fun getResultDamage(@Nullable entity: Entity?): Int {
        return resultDamage
    }

    val resultDamage: Int
        get() = NukkitMath.ceilDouble(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage())

    fun attack(source: EntityDamageEvent): Boolean {
        return source.getCause() === DamageCause.VOID && super.attack(source)
    }

    fun onCollideWithEntity(entity: Entity) {
        val projectileHitEvent = ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity))
        this.server.getPluginManager().callEvent(projectileHitEvent)
        if (projectileHitEvent.isCancelled()) {
            return
        }
        val damage = getResultDamage(entity).toFloat()
        val ev: EntityDamageEvent
        if (shootingEntity == null) {
            ev = EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage)
        } else {
            ev = EntityDamageByChildEntityEvent(shootingEntity, this, entity, DamageCause.PROJECTILE, damage)
        }
        if (entity.attack(ev)) {
            addHitEffect()
            hadCollision = true
            if (this.fireTicks > 0) {
                val event = EntityCombustByEntityEvent(this, entity, 5)
                this.server.getPluginManager().callEvent(ev)
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration())
                }
            }
        }
        afterCollisionWithEntity(entity)
        if (closeOnCollide) {
            this.close()
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun afterCollisionWithEntity(entity: Entity?) {
    }

    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(1)
        this.setHealth(1)
        if (this.namedTag.contains("Age") && hasAge) {
            this.age = this.namedTag.getShort("Age")
        }
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return (entity is EntityLiving || entity is EntityEndCrystal) && !this.onGround
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        if (hasAge) {
            this.namedTag.putShort("Age", this.age)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun updateMotion() {
        this.motionY -= this.getGravity()
        this.motionX *= 1 - this.getDrag()
        this.motionZ *= 1 - this.getDrag()
    }

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        val tickDiff: Int = currentTick - this.lastUpdate
        if (tickDiff <= 0 && !this.justCreated) {
            return true
        }
        this.lastUpdate = currentTick
        var hasUpdate: Boolean = this.entityBaseTick(tickDiff)
        if (this.isAlive()) {
            var movingObjectPosition: MovingObjectPosition? = null
            if (!this.isCollided) {
                updateMotion()
            }
            val moveVector = Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ)
            val list: Array<Entity> = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this)
            var nearDistance: Double = Integer.MAX_VALUE
            var nearEntity: Entity? = null
            for (entity in list) {
                if ( /*!entity.canCollideWith(this) or */entity === shootingEntity && this.ticksLived < 5 ||
                        entity is Player && (entity as Player).getGamemode() === Player.SPECTATOR) {
                    continue
                }
                val axisalignedbb: AxisAlignedBB = entity.boundingBox.grow(0.3, 0.3, 0.3)
                val ob: MovingObjectPosition = axisalignedbb.calculateIntercept(this, moveVector) ?: continue
                val distance: Double = this.distanceSquared(ob.hitVector)
                if (distance < nearDistance) {
                    nearDistance = distance
                    nearEntity = entity
                }
            }
            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity)
            }
            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit)
                    hasUpdate = true
                    if (closed) {
                        return true
                    }
                }
            }
            val position: Position = getPosition()
            val motion: Vector3 = getMotion()
            this.move(this.motionX, this.motionY, this.motionZ)
            if (this.isCollided && !hadCollision) { //collide with block
                hadCollision = true
                this.motionX = 0
                this.motionY = 0
                this.motionZ = 0
                this.server.getPluginManager().callEvent(ProjectileHitEvent(this, MovingObjectPosition.fromBlock(this.getFloorX(), this.getFloorY(), this.getFloorZ(), -1, this)))
                onCollideWithBlock(position, motion)
                addHitEffect()
                return false
            } else if (!this.isCollided && hadCollision) {
                hadCollision = false
            }
            if (!hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation()
                hasUpdate = true
            }
            this.updateMovement()
        }
        return hasUpdate
    }

    fun updateRotation() {
        val f: Double = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)
        this.yaw = Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI
        this.pitch = Math.atan2(this.motionY, f) * 180 / Math.PI
    }

    fun inaccurate(modifier: Float) {
        val rand: Random = ThreadLocalRandom.current()
        this.motionX += rand.nextGaussian() * 0.007499999832361937 * modifier
        this.motionY += rand.nextGaussian() * 0.007499999832361937 * modifier
        this.motionZ += rand.nextGaussian() * 0.007499999832361937 * modifier
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun onCollideWithBlock(position: Position?, motion: Vector3?) {
        for (collisionBlock in level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            onCollideWithBlock(position, motion, collisionBlock)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun onCollideWithBlock(position: Position?, motion: Vector3?, collisionBlock: Block): Boolean {
        return collisionBlock.onProjectileHit(this, position, motion)
    }

    @PowerNukkitOnly
    protected fun addHitEffect() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasAge(): Boolean {
        return hasAge
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setAge(hasAge: Boolean) {
        this.hasAge = hasAge
    }

    companion object {
        const val DATA_SHOOTER_ID = 17
    }

    init {
        this.shootingEntity = shootingEntity
        if (shootingEntity != null) {
            this.setDataProperty(LongEntityData(DATA_SHOOTER_ID, shootingEntity.getId()))
        }
    }
}