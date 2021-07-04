package cn.nukkit.entity.projectile

import cn.nukkit.Player

/**
 * @author PetteriM1
 * @author GoodLucky777
 */
class EntityThrownTrident(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?, critical: Boolean) : EntityProjectile(chunk, nbt, shootingEntity) {
    // NBT data
    protected var trident: Item? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var collisionPos: Vector3? = null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var stuckToBlockPos: BlockVector3? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var favoredSlot = 0

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var isCreative = false

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var isPlayer = false

    // Enchantment
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var loyaltyLevel = 0

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var hasChanneling = false

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var riptideLevel = 0

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var impalingLevel = 0

    // Default Values
    protected var gravity = 0.04f
    protected var drag = 0.01f

    @get:Override
    val width: Float
        get() = 0.25f

    @get:Override
    val length: Float
        get() = 0.25f

    @get:Override
    val height: Float
        get() = 0.35f

    @Override
    fun getGravity(): Float {
        return 0.04f
    }

    @Override
    fun getDrag(): Float {
        return 0.01f
    }

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : this(chunk, nbt, null) {}
    constructor(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : this(chunk, nbt, shootingEntity, false) {}

    @get:Override
    val name: String
        get() = "Trident"

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.closeOnCollide = false
        this.hasAge = false
        if (namedTag.contains("Trident")) {
            trident = NBTIO.getItemHelper(namedTag.getCompound("Trident"))
            loyaltyLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY)
            hasChanneling = trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING)
            riptideLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE)
            impalingLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING)
        } else {
            trident = Item.get(0)
            loyaltyLevel = 0
            hasChanneling = false
            riptideLevel = 0
            impalingLevel = 0
        }
        if (namedTag.contains("damage")) {
            this.damage = namedTag.getDouble("damage")
        } else {
            this.damage = 8
        }
        if (namedTag.contains("CollisionPos")) {
            val collisionPosList: ListTag<DoubleTag> = this.namedTag.getList("CollisionPos", DoubleTag::class.java)
            collisionPos = Vector3(collisionPosList.get(0).data, collisionPosList.get(1).data, collisionPosList.get(2).data)
        } else {
            collisionPos = defaultCollisionPos.clone()
        }
        if (namedTag.contains("StuckToBlockPos")) {
            val stuckToBlockPosList: ListTag<IntTag> = this.namedTag.getList("StuckToBlockPos", IntTag::class.java)
            stuckToBlockPos = BlockVector3(stuckToBlockPosList.get(0).data, stuckToBlockPosList.get(1).data, stuckToBlockPosList.get(2).data)
        } else {
            stuckToBlockPos = defaultStuckToBlockPos.clone()
        }
        if (namedTag.contains("favoredSlot")) {
            favoredSlot = namedTag.getInt("favoredSlot")
        } else {
            favoredSlot = -1
        }
        if (namedTag.contains("isCreative")) {
            isCreative = namedTag.getBoolean("isCreative")
        } else {
            isCreative = false
        }
        if (namedTag.contains("player")) {
            isPlayer = namedTag.getBoolean("player")
        } else {
            isPlayer = true
        }
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.put("Trident", NBTIO.putItemHelper(trident))
        this.namedTag.putList(ListTag<DoubleTag>("CollisionPos")
                .add(DoubleTag("0", collisionPos.x))
                .add(DoubleTag("1", collisionPos.y))
                .add(DoubleTag("2", collisionPos.z))
        )
        this.namedTag.putList(ListTag<IntTag>("StuckToBlockPos")
                .add(IntTag("0", stuckToBlockPos.x))
                .add(IntTag("1", stuckToBlockPos.y))
                .add(IntTag("2", stuckToBlockPos.z))
        )
        this.namedTag.putInt("favoredSlot", favoredSlot)
        this.namedTag.putBoolean("isCreative", isCreative)
        this.namedTag.putBoolean("player", isPlayer)
    }

    var item: Item
        get() = if (trident != null) trident.clone() else Item.get(0)
        set(item) {
            trident = item.clone()
            loyaltyLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_LOYALTY)
            hasChanneling = trident.hasEnchantment(Enchantment.ID_TRIDENT_CHANNELING)
            riptideLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_RIPTIDE)
            impalingLevel = trident.getEnchantmentLevel(Enchantment.ID_TRIDENT_IMPALING)
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
        protected get() = 8

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        if (this.isCollided && !this.hadCollision) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT_GROUND)
        }
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.onGround || this.hadCollision) {
            isCritical = false
        }
        if (this.noClip) {
            if (canReturnToShooter()) {
                val shooter: Entity = this.shootingEntity
                val vector3 = Vector3(shooter.x - this.x, shooter.y + shooter.getEyeHeight() - this.y, shooter.z - this.z)
                this.setPosition(Vector3(this.x, this.y + vector3.y * 0.015 * loyaltyLevel.toDouble(), this.z))
                this.setMotion(this.getMotion().multiply(0.95).add(vector3.multiply(loyaltyLevel * 0.05)))
                hasUpdate = true
            } else {
                if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) && !this.closed) {
                    this.level.dropItem(this, trident)
                }
                this.close()
            }
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    @Override
    fun spawnTo(player: Player) {
        val pk = AddEntityPacket()
        pk.type = networkId
        pk.entityUniqueId = this.getId()
        pk.entityRuntimeId = this.getId()
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.speedX = this.motionX as Float
        pk.speedY = this.motionY as Float
        pk.speedZ = this.motionZ as Float
        pk.yaw = this.yaw as Float
        pk.pitch = this.pitch as Float
        pk.metadata = this.dataProperties
        player.dataPacket(pk)
        super.spawnTo(player)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onCollideWithEntity(entity: Entity) {
        if (this.noClip) {
            return
        }
        this.server.getPluginManager().callEvent(ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)))
        var damage = resultDamage.toFloat()
        if (impalingLevel > 0 && (entity.isTouchingWater() || entity.getLevel().isRaining() && entity.getLevel().canBlockSeeSky(entity))) {
            damage = damage + 2.5f * impalingLevel.toFloat()
        }
        val ev: EntityDamageEvent
        if (this.shootingEntity == null) {
            ev = EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage)
        } else {
            ev = EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage)
        }
        entity.attack(ev)
        this.getLevel().addSound(this, Sound.ITEM_TRIDENT_HIT)
        this.hadCollision = true
        setCollisionPos(this)
        this.setMotion(Vector3(this.getMotion().getX() * -0.01, this.getMotion().getY() * -0.1, this.getMotion().getZ() * -0.01))
        if (hasChanneling) {
            if (this.level.isThundering() && this.level.canBlockSeeSky(this)) {
                val pos: Position = this.getPosition()
                val lighting = EntityLightning(pos.getChunk(), getDefaultNBT(pos))
                lighting.spawnToAll()
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_THUNDER)
            }
        }
        if (canReturnToShooter()) {
            this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN)
            this.setNoClip(true)
            this.hadCollision = false
            tridentRope = true
        }
    }

    fun create(type: Object, source: Position, vararg args: Object?): Entity? {
        val chunk: FullChunk = source.getLevel().getChunk(source.x as Int shr 4, source.z as Int shr 4)
                ?: return null
        val nbt: CompoundTag = Entity.getDefaultNBT(
                source.add(0.5, 0, 0.5),
                null,
                Random().nextFloat() * 360, 0
        )
        return Entity.createEntity(type.toString(), chunk, nbt, args)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    protected override fun onCollideWithBlock(position: Position?, motion: Vector3?) {
        if (this.noClip) {
            return
        }
        for (collisionBlock in level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
            setStuckToBlockPos(BlockVector3(collisionBlock.getFloorX(), collisionBlock.getFloorY(), collisionBlock.getFloorZ()))
            if (canReturnToShooter()) {
                this.getLevel().addSound(this, Sound.ITEM_TRIDENT_RETURN)
                this.setNoClip(true)
                tridentRope = true
                return
            }
            onCollideWithBlock(position, motion, collisionBlock)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getCollisionPos(): Vector3? {
        return collisionPos
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setCollisionPos(collisionPos: Vector3?) {
        this.collisionPos = collisionPos
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getStuckToBlockPos(): BlockVector3? {
        return stuckToBlockPos
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setStuckToBlockPos(stuckToBlockPos: BlockVector3?) {
        this.stuckToBlockPos = stuckToBlockPos
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getLoyaltyLevel(): Int {
        return loyaltyLevel
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setLoyaltyLevel(loyaltyLevel: Int) {
        this.loyaltyLevel = loyaltyLevel
        if (loyaltyLevel > 0) {
            trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_LOYALTY).setLevel(loyaltyLevel))
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_LOYALTY);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasChanneling(): Boolean {
        return hasChanneling
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setChanneling(hasChanneling: Boolean) {
        this.hasChanneling = hasChanneling
        if (hasChanneling) {
            trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_CHANNELING))
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_CHANNELING);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getRiptideLevel(): Int {
        return riptideLevel
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setRiptideLevel(riptideLevel: Int) {
        this.riptideLevel = riptideLevel
        if (riptideLevel > 0) {
            trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_RIPTIDE).setLevel(riptideLevel))
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_RIPTIDE);
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getImpalingLevel(): Int {
        return impalingLevel
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setImpalingLevel(impalingLevel: Int) {
        this.impalingLevel = impalingLevel
        if (impalingLevel > 0) {
            trident.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_TRIDENT_IMPALING).setLevel(impalingLevel))
        } else {
            // TODO: this.trident.removeEnchantment(Enchantment.ID_TRIDENT_IMPALING);
        }
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var tridentRope: Boolean
        get() = this.getDataFlag(DATA_FLAGS, DATA_FLAG_SHOW_TRIDENT_ROPE)
        set(tridentRope) {
            if (tridentRope) {
                this.setDataProperty(LongEntityData(DATA_OWNER_EID, this.shootingEntity.getId()))
            } else {
                this.setDataProperty(LongEntityData(DATA_OWNER_EID, -1))
            }
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SHOW_TRIDENT_ROPE, tridentRope)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun canReturnToShooter(): Boolean {
        if (loyaltyLevel <= 0) {
            return false
        }
        if (getCollisionPos().equals(defaultCollisionPos) && getStuckToBlockPos().equals(defaultStuckToBlockPos)) {
            return false
        }
        val shooter: Entity = this.shootingEntity
        if (shooter != null) {
            if (shooter.isAlive() && shooter is Player) {
                return !(shooter as Player).isSpectator()
            }
        }
        return false
    }

    companion object {
        @get:Override
        val networkId = 73
            get() = Companion.field
        const val DATA_SOURCE_ID = 17

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        private val defaultCollisionPos: Vector3 = Vector3(0, 0, 0)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        private val defaultStuckToBlockPos: BlockVector3 = BlockVector3(0, 0, 0)
    }
}