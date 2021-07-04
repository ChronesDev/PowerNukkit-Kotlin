package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
class EntityFishingHook(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : EntityProjectile(chunk, nbt, shootingEntity) {
    var chance = false
    var waitChance = WAIT_CHANCE * 2
    var attracted = false
    var attractTimer = 0
    var caught = false
    var coughtTimer = 0
    var fish: Vector3? = null
    var rod: Item? = null

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}

    @Override
    protected fun initEntity() {
        super.initEntity()
        // https://github.com/PowerNukkit/PowerNukkit/issues/267
        if (age > 0) {
            close()
        }
    }

    @get:Override
    val width: Float
        get() = 0.2f

    @get:Override
    val length: Float
        get() = 0.2f

    @get:Override
    val height: Float
        get() = 0.2f

    @get:Override
    val gravity: Float
        get() = 0.05f

    @get:Override
    val drag: Float
        get() = 0.04f

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        var hasUpdate = false
        val target: Long = getDataPropertyLong(DATA_TARGET_EID)
        if (target != 0L) {
            val entity: Entity = getLevel().getEntity(target)
            if (entity == null || !entity.isAlive()) {
                setDataProperty(LongEntityData(DATA_TARGET_EID, 0L))
            } else {
                val offset: Vector3f = entity.getMountedOffset(this)
                setPosition(Vector3(entity.x + offset.x, entity.y + offset.y, entity.z + offset.z))
            }
            hasUpdate = true
        }
        hasUpdate = hasUpdate or super.onUpdate(currentTick)
        if (hasUpdate) {
            return false
        }
        if (this.isInsideOfWater()) {
            this.motionX = 0
            this.motionY -= gravity * -0.04
            this.motionZ = 0
            hasUpdate = true
        } else if (this.isCollided && this.keepMovement) {
            this.motionX = 0
            this.motionY = 0
            this.motionZ = 0
            this.keepMovement = false
            hasUpdate = true
        }
        val random = Random()
        if (this.isInsideOfWater()) {
            if (!attracted) {
                if (waitChance > 0) {
                    --waitChance
                }
                if (waitChance == 0) {
                    if (random.nextInt(100) < 90) {
                        attractTimer = random.nextInt(40) + 20
                        spawnFish()
                        caught = false
                        attracted = true
                    } else {
                        waitChance = WAIT_CHANCE
                    }
                }
            } else if (!caught) {
                if (attractFish()) {
                    coughtTimer = random.nextInt(20) + 30
                    fishBites()
                    caught = true
                }
            } else {
                if (coughtTimer > 0) {
                    --coughtTimer
                }
                if (coughtTimer == 0) {
                    attracted = false
                    caught = false
                    waitChance = WAIT_CHANCE * 3
                }
            }
        }
        return hasUpdate
    }

    val waterHeight: Int
        get() {
            for (y in this.getFloorY()..255) {
                val id: Int = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ())
                if (id == Block.AIR) {
                    return y
                }
            }
            return this.getFloorY()
        }

    fun fishBites() {
        val pk = EntityEventPacket()
        pk.eid = this.getId()
        pk.event = EntityEventPacket.FISH_HOOK_HOOK
        Server.broadcastPacket(this.getViewers().values(), pk)
        val bubblePk = EntityEventPacket()
        bubblePk.eid = this.getId()
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE
        Server.broadcastPacket(this.getViewers().values(), bubblePk)
        val teasePk = EntityEventPacket()
        teasePk.eid = this.getId()
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE
        Server.broadcastPacket(this.getViewers().values(), teasePk)
        val random = Random()
        for (i in 0..4) {
            this.level.addParticle(BubbleParticle(this.setComponents(
                    this.x + random.nextDouble() * 0.5 - 0.25,
                    waterHeight,
                    this.z + random.nextDouble() * 0.5 - 0.25
            )))
        }
    }

    fun spawnFish() {
        val random = Random()
        fish = Vector3(
                this.x + (random.nextDouble() * 1.2 + 1) * if (random.nextBoolean()) -1 else 1,
                waterHeight,
                this.z + (random.nextDouble() * 1.2 + 1) * if (random.nextBoolean()) -1 else 1
        )
    }

    fun attractFish(): Boolean {
        val multiply = 0.1
        fish.setComponents(
                fish.x + (this.x - fish.x) * multiply,
                fish.y,
                fish.z + (this.z - fish.z) * multiply
        )
        if (Random().nextInt(100) < 85) {
            this.level.addParticle(WaterParticle(fish))
        }
        val dist: Double = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(fish.x * fish.x + fish.z * fish.z))
        return if (dist < 0.15) {
            true
        } else false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "May create custom EntityItem")
    fun reelLine() {
        if (this.shootingEntity is Player && caught) {
            val player: Player = this.shootingEntity as Player
            val item: Item = Fishing.getFishingResult(rod)
            val experience: Int = ThreadLocalRandom.current().nextInt(3) + 1
            val motion: Vector3 = player.subtract(this).multiply(0.1)
            motion.y += Math.sqrt(player.distance(this)) * 0.08
            val event = PlayerFishEvent(player, this, item, experience, motion)
            this.getServer().getPluginManager().callEvent(event)
            if (!event.isCancelled()) {
                val itemEntity: EntityItem = Entity.createEntity(EntityItem.NETWORK_ID,
                        this.level.getChunk(this.x as Int shr 4, this.z as Int shr 4, true),
                        Entity.getDefaultNBT(
                                Vector3(this.x, waterHeight, this.z),
                                event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360,
                                0
                        ).putCompound("Item", NBTIO.putItemHelper(event.getLoot()))
                                .putShort("Health", 5)
                                .putShort("PickupDelay", 1))
                if (itemEntity != null) {
                    itemEntity.setOwner(player.getName())
                    itemEntity.spawnToAll()
                    player.addExperience(event.getExperience())
                }
            }
        }
        this.close()
    }

    @Override
    fun spawnTo(player: Player) {
        val pk = AddEntityPacket()
        pk.entityRuntimeId = this.getId()
        pk.entityUniqueId = this.getId()
        pk.type = networkId
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.speedX = this.motionX as Float
        pk.speedY = this.motionY as Float
        pk.speedZ = this.motionZ as Float
        pk.yaw = this.yaw as Float
        pk.pitch = this.pitch as Float
        var ownerId: Long = -1
        if (this.shootingEntity != null) {
            ownerId = this.shootingEntity.getId()
        }
        pk.metadata = this.dataProperties.putLong(DATA_OWNER_EID, ownerId)
        player.dataPacket(pk)
        super.spawnTo(player)
    }

    @Override
    fun canCollide(): Boolean {
        return getDataPropertyLong(DATA_TARGET_EID) === 0L
    }

    @Override
    fun onCollideWithEntity(entity: Entity) {
        this.server.getPluginManager().callEvent(ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)))
        val damage: Float = this.getResultDamage()
        val ev: EntityDamageEvent
        if (this.shootingEntity == null) {
            ev = EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage)
        } else {
            ev = EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage)
        }
        if (entity.attack(ev)) {
            setDataProperty(LongEntityData(DATA_TARGET_EID, entity.getId()))
        }
    }

    companion object {
        @get:Override
        val networkId = 77
            get() = Companion.field
        const val WAIT_CHANCE = 120
        const val CHANCE = 40
    }
}