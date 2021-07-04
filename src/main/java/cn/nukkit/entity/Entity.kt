package cn.nukkit.entity

import cn.nukkit.AdventureSettings

/**
 * @author MagicDroidX
 */
@Log4j2
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "All DATA constants were made dynamic because they have tendency to change on Minecraft updates, " +
        "these dynamic calls will avoid the need of plugin recompilations after Minecraft updates that " +
        "shifts the data values")
abstract class Entity(chunk: FullChunk?, nbt: CompoundTag?) : Location(), Metadatable {
    abstract val networkId: Int
    protected val hasSpawned: Map<Integer, Player> = ConcurrentHashMap()
    protected val effects: Map<Integer, Effect> = ConcurrentHashMap()
    var id: Long = 0
        protected set
    protected val dataProperties: EntityMetadata = EntityMetadata()
            .putLong(DATA_FLAGS, 0)
            .putByte(DATA_COLOR, 0)
            .putShort(DATA_AIR, 400)
            .putShort(DATA_MAX_AIR, 400)
            .putString(DATA_NAMETAG, "")
            .putLong(DATA_LEAD_HOLDER_EID, -1)
            .putFloat(DATA_SCALE, 1f)
    val passengers: List<Entity> = ArrayList()
    var riding: Entity? = null
    var chunk: FullChunk? = null
    protected var lastDamageCause: EntityDamageEvent? = null
    var blocksAround: List<Block>? = ArrayList()
    var collisionBlocks: List<Block>? = ArrayList()
    var lastX = 0.0
    var lastY = 0.0
    var lastZ = 0.0
    var firstMove = true
    var motionX = 0.0
    var motionY = 0.0
    var motionZ = 0.0
    var temporalVector: Vector3? = null
    var lastMotionX = 0.0
    var lastMotionY = 0.0
    var lastMotionZ = 0.0
    var lastYaw = 0.0
    var lastPitch = 0.0
    var pitchDelta = 0.0
    var yawDelta = 0.0
    var entityCollisionReduction = 0.0 // Higher than 0.9 will result a fast collisions
    var boundingBox: AxisAlignedBB? = null
    var isOnGround = false
    var inBlock = false
    var positionChanged = false
    var motionChanged = false
    var deadTicks = 0
    protected var age = 0
    protected var health = 20f
    var maxHealth = 20
        get() = field + if (hasEffect(Effect.HEALTH_BOOST)) 4 * (getEffect(Effect.HEALTH_BOOST).getAmplifier() + 1) else 0
    protected var absorption = 0f
    protected var ySize = 0f
    var keepMovement = false
    var fallDistance = 0f
    var ticksLived = 0
    var lastUpdate = 0
    var maxFireTicks = 0
    var fireTicks = 0
    var inPortalTicks = 0

    @get:Since("1.2.1.0-PN")
    @get:PowerNukkitOnly
    @PowerNukkitOnly
    @Since("1.2.1.0-PN")
    var isInEndPortal = false
        protected set
    var scale = 1f
    var namedTag: CompoundTag? = null
    protected var isStatic = false
    var isCollided = false
    var isCollidedHorizontally = false
    var isCollidedVertically = false
    var noDamageTicks = 0
    var justCreated = false
    var fireProof = false
    var invulnerable = false
    protected var server: Server? = null
    var highestPosition = 0.0
    var isClosed = false
    protected var timing: Timing? = null
    protected var isPlayer = false

    @Volatile
    private var initialized = false

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var noClip = false
    val height: Float
        get() = 0
    val eyeHeight: Float
        get() = height / 2 + 0.1f
    val width: Float
        get() = 0
    val length: Float
        get() = 0
    protected val stepHeight: Double
        protected get() = 0

    fun canCollide(): Boolean {
        return true
    }

    protected val gravity: Float
        protected get() = 0
    protected val drag: Float
        protected get() = 0
    protected val baseOffset: Float
        protected get() = 0

    protected fun initEntity() {
        if (namedTag.contains("ActiveEffects")) {
            val effects: ListTag<CompoundTag> = namedTag.getList("ActiveEffects", CompoundTag::class.java)
            for (e in effects.getAll()) {
                val effect: Effect = Effect.getEffect(e.getByte("Id")) ?: continue
                effect.setAmplifier(e.getByte("Amplifier")).setDuration(e.getInt("Duration")).setVisible(e.getBoolean("ShowParticles"))
                addEffect(effect)
            }
        }
        if (namedTag.contains("CustomName")) {
            nameTag = namedTag.getString("CustomName")
            if (namedTag.contains("CustomNameVisible")) {
                isNameTagVisible = namedTag.getBoolean("CustomNameVisible")
            }
            if (namedTag.contains("CustomNameAlwaysVisible")) {
                isNameTagAlwaysVisible = namedTag.getBoolean("CustomNameAlwaysVisible")
            }
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, true)
        dataProperties.putFloat(DATA_BOUNDING_BOX_HEIGHT, height)
        dataProperties.putFloat(DATA_BOUNDING_BOX_WIDTH, width)
        dataProperties.putInt(DATA_HEALTH, getHealth().toInt())
        scheduleUpdate()
    }

    protected fun init(chunk: FullChunk?, nbt: CompoundTag?) {
        if (chunk == null || chunk.getProvider() == null) {
            throw ChunkException("Invalid garbage Chunk given to Entity")
        }
        if (initialized) {
            // We've already initialized this entity
            return
        }
        initialized = true
        timing = Timings.getEntityTiming(this)
        isPlayer = this is Player
        temporalVector = Vector3()
        id = entityCount++
        justCreated = true
        namedTag = nbt
        this.chunk = chunk
        this.setLevel(chunk.getProvider().getLevel())
        server = chunk.getProvider().getLevel().getServer()
        boundingBox = SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0)
        val posList: ListTag<DoubleTag> = namedTag.getList("Pos", DoubleTag::class.java)
        val rotationList: ListTag<FloatTag> = namedTag.getList("Rotation", FloatTag::class.java)
        val motionList: ListTag<DoubleTag> = namedTag.getList("Motion", DoubleTag::class.java)
        setPositionAndRotation(
                temporalVector.setComponents(
                        posList.get(0).data,
                        posList.get(1).data,
                        posList.get(2).data
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        )
        setMotion(temporalVector.setComponents(
                motionList.get(0).data,
                motionList.get(1).data,
                motionList.get(2).data
        ))
        if (!namedTag.contains("FallDistance")) {
            namedTag.putFloat("FallDistance", 0)
        }
        fallDistance = namedTag.getFloat("FallDistance")
        highestPosition = this.y + namedTag.getFloat("FallDistance")
        if (!namedTag.contains("Fire") || namedTag.getShort("Fire") > 32767) {
            namedTag.putShort("Fire", 0)
        }
        fireTicks = namedTag.getShort("Fire")
        if (!namedTag.contains("Air")) {
            namedTag.putShort("Air", 300)
        }
        this.setDataProperty(ShortEntityData(DATA_AIR, namedTag.getShort("Air")), false)
        if (!namedTag.contains("OnGround")) {
            namedTag.putBoolean("OnGround", false)
        }
        isOnGround = namedTag.getBoolean("OnGround")
        if (!namedTag.contains("Invulnerable")) {
            namedTag.putBoolean("Invulnerable", false)
        }
        invulnerable = namedTag.getBoolean("Invulnerable")
        if (!namedTag.contains("Scale")) {
            namedTag.putFloat("Scale", 1)
        }
        scale = namedTag.getFloat("Scale")
        this.setDataProperty(FloatEntityData(DATA_SCALE, scale), false)
        this.setDataProperty(ByteEntityData(DATA_COLOR, 0), false)
        try {
            this.chunk.addEntity(this)
            this.level.addEntity(this)
            initEntity()
            lastUpdate = server.getTick()
            val event = EntitySpawnEvent(this)
            server.getPluginManager().callEvent(event)
            if (event.isCancelled()) {
                this.close(false)
            } else {
                scheduleUpdate()
            }
        } catch (e: Exception) {
            this.close(false)
            throw e
        }
    }

    fun hasCustomName(): Boolean {
        return !nameTag.isEmpty()
    }

    var nameTag: String?
        get() = getDataPropertyString(DATA_NAMETAG)
        set(name) {
            this.setDataProperty(StringEntityData(DATA_NAMETAG, name))
        }
    var isNameTagVisible: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_CAN_SHOW_NAMETAG)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_SHOW_NAMETAG, value)
        }
    var isNameTagAlwaysVisible: Boolean
        get() = getDataPropertyByte(DATA_ALWAYS_SHOW_NAMETAG) == 1
        set(value) {
            this.setDataProperty(ByteEntityData(DATA_ALWAYS_SHOW_NAMETAG, if (value) 1 else 0))
        }

    fun setNameTagVisible() {
        isNameTagVisible = true
    }

    fun setNameTagAlwaysVisible() {
        isNameTagAlwaysVisible = true
    }

    var scoreTag: String?
        get() = getDataPropertyString(DATA_SCORE_TAG)
        set(score) {
            this.setDataProperty(StringEntityData(DATA_SCORE_TAG, score))
        }
    var isSneaking: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SNEAKING, value)
        }

    fun setSneaking() {
        isSneaking = true
    }

    var isSwimming: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_SWIMMING)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SWIMMING, value)
        }

    fun setSwimming() {
        isSwimming = true
    }

    var isSprinting: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPRINTING, value)
        }

    fun setSprinting() {
        isSprinting = true
    }

    var isGliding: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_GLIDING)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_GLIDING, value)
        }

    fun setGliding() {
        isGliding = true
    }

    var isImmobile: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE, value)
        }

    fun setImmobile() {
        isImmobile = true
    }

    fun canClimb(): Boolean {
        return getDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB)
    }

    fun setCanClimb() {
        this.setCanClimb(true)
    }

    fun setCanClimb(value: Boolean) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_CAN_CLIMB, value)
    }

    fun canClimbWalls(): Boolean {
        return getDataFlag(DATA_FLAGS, DATA_FLAG_WALLCLIMBING)
    }

    fun setCanClimbWalls() {
        this.setCanClimbWalls(true)
    }

    fun setCanClimbWalls(value: Boolean) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_WALLCLIMBING, value)
    }

    fun setScale(scale: Float) {
        this.scale = scale
        this.setDataProperty(FloatEntityData(DATA_SCALE, this.scale))
        recalculateBoundingBox()
    }

    fun getScale(): Float {
        return scale
    }

    val passenger: Entity
        get() = Iterables.getFirst(passengers, null)

    fun isPassenger(entity: Entity): Boolean {
        return passengers.contains(entity)
    }

    fun isControlling(entity: Entity): Boolean {
        return passengers.indexOf(entity) === 0
    }

    fun hasControllingPassenger(): Boolean {
        return !passengers.isEmpty() && isControlling(passengers[0])
    }

    fun getEffects(): Map<Integer, Effect> {
        return effects
    }

    fun removeAllEffects() {
        for (effect in effects.values()) {
            removeEffect(effect.getId())
        }
    }

    fun removeEffect(effectId: Int) {
        if (effects.containsKey(effectId)) {
            val effect: Effect? = effects[effectId]
            effects.remove(effectId)
            effect.remove(this)
            recalculateEffectColor()
        }
    }

    fun getEffect(effectId: Int): Effect {
        return effects.getOrDefault(effectId, null)
    }

    fun hasEffect(effectId: Int): Boolean {
        return effects.containsKey(effectId)
    }

    fun addEffect(effect: Effect?) {
        if (effect == null) {
            return  //here add null means add nothing
        }
        effect.add(this)
        effects.put(effect.getId(), effect)
        recalculateEffectColor()
        if (effect.getId() === Effect.HEALTH_BOOST) {
            setHealth(getHealth() + 4 * (effect.getAmplifier() + 1))
        }
    }

    @JvmOverloads
    fun recalculateBoundingBox(send: Boolean = true) {
        val height = height * scale
        val radius = width * scale / 2.0
        boundingBox.setBounds(x - radius, y, z - radius, x + radius, y + height, z + radius)
        val bbH = FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, this.height)
        val bbW = FloatEntityData(DATA_BOUNDING_BOX_WIDTH, width)
        dataProperties.put(bbH)
        dataProperties.put(bbW)
        if (send) {
            sendData(hasSpawned.values().toArray(Player.EMPTY_ARRAY), EntityMetadata().put(bbH).put(bbW))
        }
    }

    protected fun recalculateEffectColor() {
        val color = IntArray(3)
        var count = 0
        var ambient = true
        for (effect in effects.values()) {
            if (effect.isVisible()) {
                val c: IntArray = effect.getColor()
                color[0] += c[0] * (effect.getAmplifier() + 1)
                color[1] += c[1] * (effect.getAmplifier() + 1)
                color[2] += c[2] * (effect.getAmplifier() + 1)
                count += effect.getAmplifier() + 1
                if (!effect.isAmbient()) {
                    ambient = false
                }
            }
        }
        if (count > 0) {
            val r = color[0] / count and 0xff
            val g = color[1] / count and 0xff
            val b = color[2] / count and 0xff
            this.setDataProperty(IntEntityData(DATA_POTION_COLOR, (r shl 16) + (g shl 8) + b))
            this.setDataProperty(ByteEntityData(DATA_POTION_AMBIENT, if (ambient) 1 else 0))
        } else {
            this.setDataProperty(IntEntityData(DATA_POTION_COLOR, 0))
            this.setDataProperty(ByteEntityData(DATA_POTION_AMBIENT, 0))
        }
    }

    fun saveNBT() {
        if (this !is Player) {
            namedTag.putString("id", saveId)
            if (!nameTag!!.equals("")) {
                namedTag.putString("CustomName", nameTag)
                namedTag.putBoolean("CustomNameVisible", isNameTagVisible)
                namedTag.putBoolean("CustomNameAlwaysVisible", isNameTagAlwaysVisible)
            } else {
                namedTag.remove("CustomName")
                namedTag.remove("CustomNameVisible")
                namedTag.remove("CustomNameAlwaysVisible")
            }
        }
        namedTag.putList(ListTag<DoubleTag>("Pos")
                .add(DoubleTag("0", this.x))
                .add(DoubleTag("1", this.y))
                .add(DoubleTag("2", this.z))
        )
        namedTag.putList(ListTag<DoubleTag>("Motion")
                .add(DoubleTag("0", motionX))
                .add(DoubleTag("1", motionY))
                .add(DoubleTag("2", motionZ))
        )
        namedTag.putList(ListTag<FloatTag>("Rotation")
                .add(FloatTag("0", this.yaw as Float))
                .add(FloatTag("1", this.pitch as Float))
        )
        namedTag.putFloat("FallDistance", fallDistance)
        namedTag.putShort("Fire", fireTicks)
        namedTag.putShort("Air", getDataPropertyShort(DATA_AIR))
        namedTag.putBoolean("OnGround", isOnGround)
        namedTag.putBoolean("Invulnerable", invulnerable)
        namedTag.putFloat("Scale", scale)
        if (!effects.isEmpty()) {
            val list: ListTag<CompoundTag> = ListTag("ActiveEffects")
            for (effect in effects.values()) {
                list.add(CompoundTag(String.valueOf(effect.getId()))
                        .putByte("Id", effect.getId())
                        .putByte("Amplifier", effect.getAmplifier())
                        .putInt("Duration", effect.getDuration())
                        .putBoolean("Ambient", false)
                        .putBoolean("ShowParticles", effect.isVisible())
                )
            }
            namedTag.putList(list)
        } else {
            namedTag.remove("ActiveEffects")
        }
    }

    @get:Nonnull
    val name: String
        get() = if (hasCustomName()) {
            nameTag!!
        } else {
            saveId
        }
    val saveId: String
        get() = shortNames.getOrDefault(this.getClass().getSimpleName(), "")

    fun spawnTo(player: Player) {
        if (!hasSpawned.containsKey(player.getLoaderId()) && chunk != null && player.usedChunks.containsKey(Level.chunkHash(chunk.getX(), chunk.getZ()))) {
            hasSpawned.put(player.getLoaderId(), player)
            player.dataPacket(createAddEntityPacket())
        }
        if (riding != null) {
            riding!!.spawnTo(player)
            val pkk = SetEntityLinkPacket()
            pkk.vehicleUniqueId = riding!!.id
            pkk.riderUniqueId = id
            pkk.type = 1
            pkk.immediate = 1
            player.dataPacket(pkk)
        }
    }

    protected fun createAddEntityPacket(): DataPacket {
        val addEntity = AddEntityPacket()
        addEntity.type = networkId
        addEntity.entityUniqueId = id
        addEntity.entityRuntimeId = id
        addEntity.yaw = this.yaw as Float
        addEntity.headYaw = this.yaw as Float
        addEntity.pitch = this.pitch as Float
        addEntity.x = this.x as Float
        addEntity.y = this.y as Float
        addEntity.z = this.z as Float
        addEntity.speedX = motionX.toFloat()
        addEntity.speedY = motionY.toFloat()
        addEntity.speedZ = motionZ.toFloat()
        addEntity.metadata = dataProperties
        addEntity.links = arrayOfNulls<EntityLink>(passengers.size())
        for (i in 0 until addEntity.links.length) {
            addEntity.links.get(i) = EntityLink(id, passengers[i].id, if (i == 0) EntityLink.TYPE_RIDER else TYPE_PASSENGER, false, false)
        }
        return addEntity
    }

    val viewers: Map<Any, Any>
        get() = hasSpawned

    fun sendPotionEffects(player: Player) {
        for (effect in effects.values()) {
            val pk = MobEffectPacket()
            pk.eid = id
            pk.effectId = effect.getId()
            pk.amplifier = effect.getAmplifier()
            pk.particles = effect.isVisible()
            pk.duration = effect.getDuration()
            pk.eventId = MobEffectPacket.EVENT_ADD
            player.dataPacket(pk)
        }
    }

    fun sendData(player: Player?) {
        this.sendData(player, null)
    }

    fun sendData(player: Player, data: EntityMetadata?) {
        val pk = SetEntityDataPacket()
        pk.eid = id
        pk.metadata = if (data == null) dataProperties else data
        player.dataPacket(pk)
    }

    fun sendData(players: Array<Player?>?) {
        this.sendData(players, null)
    }

    fun sendData(players: Array<Player>, data: EntityMetadata?) {
        val pk = SetEntityDataPacket()
        pk.eid = id
        pk.metadata = if (data == null) dataProperties else data
        for (player in players) {
            if (player === this) {
                continue
            }
            player.dataPacket(pk.clone())
        }
        if (this is Player) {
            (this as Player).dataPacket(pk)
        }
    }

    fun despawnFrom(player: Player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            val pk = RemoveEntityPacket()
            pk.eid = id
            player.dataPacket(pk)
            hasSpawned.remove(player.getLoaderId())
        }
    }

    fun attack(source: EntityDamageEvent): Boolean {
        if (hasEffect(Effect.FIRE_RESISTANCE)
                && (source.getCause() === DamageCause.FIRE || source.getCause() === DamageCause.FIRE_TICK || source.getCause() === DamageCause.LAVA)) {
            return false
        }
        getServer().getPluginManager().callEvent(source)
        if (source.isCancelled()) {
            return false
        }
        if (absorption > 0) {  // Damage Absorption
            setAbsorption(Math.max(0, getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)))
        }
        setLastDamageCause(source)
        val newHealth: Float = getHealth() - source.getFinalDamage()
        if (newHealth < 1 && this is Player) {
            if (source.getCause() !== DamageCause.VOID && source.getCause() !== DamageCause.SUICIDE) {
                val p: Player = this as Player
                var totem = false
                if (p.getOffhandInventory().getItem(0).getId() === ItemID.TOTEM) {
                    p.getOffhandInventory().clear(0)
                    totem = true
                } else if (p.getInventory().getItemInHand().getId() === ItemID.TOTEM) {
                    p.getInventory().clear(p.getInventory().getHeldItemIndex())
                    totem = true
                }
                if (totem) {
                    this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_TOTEM)
                    this.getLevel().addParticleEffect(this, ParticleEffect.TOTEM)
                    extinguish()
                    removeAllEffects()
                    setHealth(1f)
                    addEffect(Effect.getEffect(Effect.REGENERATION).setDuration(800).setAmplifier(1))
                    addEffect(Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(800))
                    addEffect(Effect.getEffect(Effect.ABSORPTION).setDuration(100).setAmplifier(1))
                    val pk = EntityEventPacket()
                    pk.eid = id
                    pk.event = EntityEventPacket.CONSUME_TOTEM
                    p.dataPacket(pk)
                    source.setCancelled(true)
                    return false
                }
            }
        }
        setHealth(newHealth)
        return true
    }

    fun attack(damage: Float): Boolean {
        return this.attack(EntityDamageEvent(this, DamageCause.CUSTOM, damage))
    }

    fun heal(source: EntityRegainHealthEvent) {
        server.getPluginManager().callEvent(source)
        if (source.isCancelled()) {
            return
        }
        setHealth(getHealth() + source.getAmount())
    }

    fun heal(amount: Float) {
        this.heal(EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN))
    }

    fun getHealth(): Float {
        return health
    }

    val isAlive: Boolean
        get() = health > 0

    fun setHealth(health: Float) {
        if (this.health == health) {
            return
        }
        if (health < 1) {
            if (isAlive) {
                kill()
            }
        } else if (health <= maxHealth || health < this.health) {
            this.health = health
        } else {
            this.health = maxHealth.toFloat()
        }
        setDataProperty(IntEntityData(DATA_HEALTH, this.health.toInt()))
    }

    fun setLastDamageCause(type: EntityDamageEvent?) {
        lastDamageCause = type
    }

    fun getLastDamageCause(): EntityDamageEvent? {
        return lastDamageCause
    }

    fun canCollideWith(entity: Entity): Boolean {
        return !justCreated && this !== entity
    }

    protected fun checkObstruction(x: Double, y: Double, z: Double): Boolean {
        if (this.level.getCollisionCubes(this, getBoundingBox(), false).length === 0 || noClip) {
            return false
        }
        val i: Int = NukkitMath.floorDouble(x)
        val j: Int = NukkitMath.floorDouble(y)
        val k: Int = NukkitMath.floorDouble(z)
        val diffX = x - i
        val diffY = y - j
        val diffZ = z - k
        if (!Block.transparent.get(this.level.getBlockIdAt(i, j, k))) {
            val flag: Boolean = Block.transparent.get(this.level.getBlockIdAt(i - 1, j, k))
            val flag1: Boolean = Block.transparent.get(this.level.getBlockIdAt(i + 1, j, k))
            val flag2: Boolean = Block.transparent.get(this.level.getBlockIdAt(i, j - 1, k))
            val flag3: Boolean = Block.transparent.get(this.level.getBlockIdAt(i, j + 1, k))
            val flag4: Boolean = Block.transparent.get(this.level.getBlockIdAt(i, j, k - 1))
            val flag5: Boolean = Block.transparent.get(this.level.getBlockIdAt(i, j, k + 1))
            var direction = -1
            var limit = 9999.0
            if (flag) {
                limit = diffX
                direction = 0
            }
            if (flag1 && 1 - diffX < limit) {
                limit = 1 - diffX
                direction = 1
            }
            if (flag2 && diffY < limit) {
                limit = diffY
                direction = 2
            }
            if (flag3 && 1 - diffY < limit) {
                limit = 1 - diffY
                direction = 3
            }
            if (flag4 && diffZ < limit) {
                limit = diffZ
                direction = 4
            }
            if (flag5 && 1 - diffZ < limit) {
                direction = 5
            }
            val force: Double = ThreadLocalRandom.current().nextDouble() * 0.2 + 0.1
            if (direction == 0) {
                motionX = -force
                return true
            }
            if (direction == 1) {
                motionX = force
                return true
            }
            if (direction == 2) {
                motionY = -force
                return true
            }
            if (direction == 3) {
                motionY = force
                return true
            }
            if (direction == 4) {
                motionZ = -force
                return true
            }
            if (direction == 5) {
                motionZ = force
                return true
            }
        }
        return false
    }

    @JvmOverloads
    fun entityBaseTick(tickDiff: Int = 1): Boolean {
        Timings.entityBaseTickTimer.startTiming()
        if (!isPlayer) {
            blocksAround = null
            collisionBlocks = null
        }
        justCreated = false
        if (!isAlive) {
            removeAllEffects()
            despawnFromAll()
            if (!isPlayer) {
                this.close()
            }
            Timings.entityBaseTickTimer.stopTiming()
            return false
        }
        if (riding != null && !riding!!.isAlive && riding is EntityRideable) {
            (riding as EntityRideable).mountEntity(this)
        }
        updatePassengers()
        if (!effects.isEmpty()) {
            for (effect in effects.values()) {
                if (effect.canTick()) {
                    effect.applyEffect(this)
                }
                effect.setDuration(effect.getDuration() - tickDiff)
                if (effect.getDuration() <= 0) {
                    removeEffect(effect.getId())
                }
            }
        }
        var hasUpdate = false
        checkBlockCollision()
        if (this.y <= -16 && isAlive) {
            if (this is Player) {
                val player: Player = this as Player
                if (!player.isCreative()) this.attack(EntityDamageEvent(this, DamageCause.VOID, 10))
            } else {
                this.attack(EntityDamageEvent(this, DamageCause.VOID, 10))
                hasUpdate = true
            }
        }
        if (fireTicks > 0) {
            if (fireProof) {
                fireTicks -= 4 * tickDiff
                if (fireTicks < 0) {
                    fireTicks = 0
                }
            } else {
                if (!hasEffect(Effect.FIRE_RESISTANCE) && (fireTicks % 20 == 0 || tickDiff > 20)) {
                    this.attack(EntityDamageEvent(this, DamageCause.FIRE_TICK, 1))
                }
                fireTicks -= tickDiff
            }
            if (fireTicks <= 0) {
                extinguish()
            } else if (!fireProof && (this !is Player || !(this as Player).isSpectator())) {
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, true)
                hasUpdate = true
            }
        }
        if (noDamageTicks > 0) {
            noDamageTicks -= tickDiff
            if (noDamageTicks < 0) {
                noDamageTicks = 0
            }
        }
        if (inPortalTicks == 80) {
            val ev = EntityPortalEnterEvent(this, PortalType.NETHER)
            getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled() && (level === EnumLevel.OVERWORLD.getLevel() || level === EnumLevel.NETHER.getLevel())) {
                val newPos: Position = EnumLevel.moveToNether(this)
                if (newPos != null) {
                    /*for (int x = -1; x < 2; x++) {
                        for (int z = -1; z < 2; z++) {
                            int chunkX = (newPos.getFloorX() >> 4) + x, chunkZ = (newPos.getFloorZ() >> 4) + z;
                            FullChunk chunk = newPos.level.getChunk(chunkX, chunkZ, false);
                            if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                                newPos.level.generateChunk(chunkX, chunkZ, true);
                            }
                        }
                    }*/
                    val nearestPortal: Position? = getNearestValidPortal(newPos)
                    if (nearestPortal != null) {
                        teleport(nearestPortal.add(0.5, 0, 0.5), PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
                    } else {
                        val finalPos: Position = newPos.add(1.5, 1, 1.5)
                        if (teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
                            server.getScheduler().scheduleDelayedTask(object : Task() {
                                @Override
                                fun onRun(currentTick: Int) {
                                    // dirty hack to make sure chunks are loaded and generated before spawning
                                    // player
                                    inPortalTicks = 81
                                    teleport(finalPos, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
                                    BlockNetherPortal.spawnPortal(newPos)
                                }
                            }, 5)
                        }
                    }
                }
            }
        }
        age += tickDiff
        ticksLived += tickDiff
        TimingsHistory.activatedEntityTicks++
        Timings.entityBaseTickTimer.stopTiming()
        return hasUpdate
    }

    private fun getNearestValidPortal(currentPos: Position): Position? {
        val axisAlignedBB: AxisAlignedBB = SimpleAxisAlignedBB(
                Vector3(currentPos.getFloorX() - 128.0, 1.0, currentPos.getFloorZ() - 128.0),
                Vector3(currentPos.getFloorX() + 128.0, if (currentPos.level.getDimension() === Level.DIMENSION_NETHER) 128 else 256, currentPos.getFloorZ() + 128.0))
        val condition: BiPredicate<BlockVector3, BlockState> = BiPredicate<BlockVector3, BlockState> { pos, state -> state.getBlockId() === BlockID.NETHER_PORTAL }
        val blocks: List<Block> = currentPos.level.scanBlocks(axisAlignedBB, condition)
        if (blocks.isEmpty()) {
            return null
        }
        val currentPosV2 = Vector2(currentPos.getFloorX(), currentPos.getFloorZ())
        val by: Double = currentPos.getFloorY()
        val euclideanDistance: Comparator<Block> = Comparator.comparingDouble { block -> currentPosV2.distanceSquared(block.getFloorX(), block.getFloorZ()) }
        val heightDistance: Comparator<Block> = Comparator.comparingDouble { block ->
            val ey: Double = by - block.y
            ey * ey
        }
        return blocks.stream()
                .filter { block -> block.down().getId() !== BlockID.NETHER_PORTAL }
                .min(euclideanDistance.thenComparing(heightDistance))
                .orElse(null) ?: return null
    }

    fun updateMovement() {
        val diffPosition: Double = (this.x - lastX) * (this.x - lastX) + (this.y - lastY) * (this.y - lastY) + (this.z - lastZ) * (this.z - lastZ)
        val diffRotation: Double = (this.yaw - lastYaw) * (this.yaw - lastYaw) + (this.pitch - lastPitch) * (this.pitch - lastPitch)
        val diffMotion = (motionX - lastMotionX) * (motionX - lastMotionX) + (motionY - lastMotionY) * (motionY - lastMotionY) + (motionZ - lastMotionZ) * (motionZ - lastMotionZ)
        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            lastX = this.x
            lastY = this.y
            lastZ = this.z
            lastYaw = this.yaw
            lastPitch = this.pitch
            addMovement(this.x, this.y + baseOffset, this.z, this.yaw, this.pitch, this.yaw)
            positionChanged = true
        } else {
            positionChanged = false
        }
        if (diffMotion > 0.0025 || diffMotion > 0.0001 && motion.lengthSquared() <= 0.0001) { //0.05 ** 2
            lastMotionX = motionX
            lastMotionY = motionY
            lastMotionZ = motionZ
            addMotion(motionX, motionY, motionZ)
        }
    }

    fun addMovement(x: Double, y: Double, z: Double, yaw: Double, pitch: Double, headYaw: Double) {
        this.level.addEntityMovement(this, x, y, z, yaw, pitch, headYaw)
    }

    fun addMotion(motionX: Double, motionY: Double, motionZ: Double) {
        val pk = SetEntityMotionPacket()
        pk.eid = id
        pk.motionX = motionX.toFloat()
        pk.motionY = motionY.toFloat()
        pk.motionZ = motionZ.toFloat()
        Server.broadcastPacket(hasSpawned.values(), pk)
    }

    val directionVector: Vector3
        get() {
            val vector: Vector3 = super.getDirectionVector()
            return temporalVector.setComponents(vector.x, vector.y, vector.z)
        }
    val directionPlane: Vector2
        get() = Vector2(-Math.cos(Math.toRadians(this.yaw) - Math.PI / 2) as Float, -Math.sin(Math.toRadians(this.yaw) - Math.PI / 2) as Float).normalize()
    val horizontalFacing: BlockFace
        get() = BlockFace.fromHorizontalIndex(NukkitMath.floorDouble(this.yaw * 4.0f / 360.0f + 0.5) and 3)

    fun onUpdate(currentTick: Int): Boolean {
        if (isClosed) {
            return false
        }
        if (!isAlive) {
            ++deadTicks
            if (deadTicks >= 10) {
                despawnFromAll()
                if (!isPlayer) {
                    this.close()
                }
            }
            return deadTicks < 10
        }
        val tickDiff = currentTick - lastUpdate
        if (tickDiff <= 0) {
            return false
        }
        lastUpdate = currentTick
        val hasUpdate = entityBaseTick(tickDiff)
        updateMovement()
        return hasUpdate
    }

    /**
     * Mount or Dismounts an Entity from a/into vehicle
     *
     * @param entity The target Entity
     * @return `true` if the mounting successful
     */
    @JvmOverloads
    fun mountEntity(entity: Entity, mode: Byte = TYPE_RIDE): Boolean {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null")
        if (entity.riding != null) {
            dismountEntity(entity)
        } else {
            if (isPassenger(entity)) {
                return false
            }

            // Entity entering a vehicle
            val ev = EntityVehicleEnterEvent(entity, this)
            server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            broadcastLinkPacket(entity, mode)

            // Add variables to entity
            entity.riding = this
            entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, true)
            passengers.add(entity)
            entity.seatPosition = getMountedOffset(entity)
            updatePassengerPosition(entity)
        }
        return true
    }

    fun dismountEntity(entity: Entity): Boolean {
        // Run the events
        val ev = EntityVehicleExitEvent(entity, this)
        server.getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        broadcastLinkPacket(entity, TYPE_REMOVE)

        // Refurbish the entity
        entity.riding = null
        entity.setDataFlag(DATA_FLAGS, DATA_FLAG_RIDING, false)
        passengers.remove(entity)
        entity.seatPosition = Vector3f()
        updatePassengerPosition(entity)
        return true
    }

    protected fun broadcastLinkPacket(rider: Entity, type: Byte) {
        val pk = SetEntityLinkPacket()
        pk.vehicleUniqueId = id // To the?
        pk.riderUniqueId = rider.id // From who?
        pk.type = type
        Server.broadcastPacket(hasSpawned.values(), pk)
    }

    fun updatePassengers() {
        if (passengers.isEmpty()) {
            return
        }
        for (passenger in ArrayList(passengers)) {
            if (!passenger.isAlive) {
                dismountEntity(passenger)
                continue
            }
            updatePassengerPosition(passenger)
        }
    }

    protected fun updatePassengerPosition(passenger: Entity) {
        passenger.setPosition(this.add(passenger.seatPosition.asVector3()))
    }

    var seatPosition: Vector3f
        get() = getDataPropertyVector3f(DATA_RIDER_SEAT_POSITION)
        set(pos) {
            this.setDataProperty(Vector3fEntityData(DATA_RIDER_SEAT_POSITION, pos))
        }

    fun getMountedOffset(entity: Entity?): Vector3f {
        return Vector3f(0, height * 0.75f)
    }

    fun scheduleUpdate() {
        this.level.updateEntities.put(id, this)
    }

    val isOnFire: Boolean
        get() = fireTicks > 0

    fun setOnFire(seconds: Int) {
        val ticks = seconds * 20
        if (ticks > fireTicks) {
            fireTicks = ticks
        }
    }

    fun getAbsorption(): Float {
        return absorption
    }

    fun setAbsorption(absorption: Float) {
        if (absorption != this.absorption) {
            this.absorption = absorption
            if (this is Player) (this as Player).setAttribute(Attribute.getAttribute(Attribute.ABSORPTION)!!.setValue(absorption))
        }
    }

    fun canBePushed(): Boolean {
        return true
    }

    val direction: BlockFace?
        get() {
            var rotation: Double = this.yaw % 360
            if (rotation < 0) {
                rotation += 360.0
            }
            return if (0 <= rotation && rotation < 45 || 315 <= rotation && rotation < 360) {
                BlockFace.SOUTH
            } else if (45 <= rotation && rotation < 135) {
                BlockFace.WEST
            } else if (135 <= rotation && rotation < 225) {
                BlockFace.NORTH
            } else if (225 <= rotation && rotation < 315) {
                BlockFace.EAST
            } else {
                null
            }
        }

    fun extinguish() {
        fireTicks = 0
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ONFIRE, false)
    }

    fun canTriggerWalking(): Boolean {
        return true
    }

    fun resetFallDistance() {
        highestPosition = 0.0
    }

    protected fun updateFallState(onGround: Boolean) {
        if (onGround) {
            fallDistance = (highestPosition - this.y) as Float
            if (fallDistance > 0) {
                // check if we fell into at least 1 block of water
                if (this is EntityLiving && this.getLevelBlock() !is BlockWater) {
                    fall(fallDistance)
                }
                resetFallDistance()
            }
        }
    }

    fun getBoundingBox(): AxisAlignedBB? {
        return boundingBox
    }

    fun fall(fallDistance: Float) {
        if (hasEffect(Effect.SLOW_FALLING)) {
            return
        }
        var damage = Math.floor(fallDistance - 3 - if (hasEffect(Effect.JUMP)) getEffect(Effect.JUMP).getAmplifier() + 1 else 0) as Float
        val floorLocation: Location = this.floor()
        val down: Block = this.level.getBlock(floorLocation.down())
        if (damage > 0) {
            if (down is BlockHayBale) {
                damage -= damage * 0.8f
            }
            if (down.getId() === BlockID.HONEY_BLOCK) {
                damage *= 0.2f
            }
            if (!isPlayer || level.getGameRules().getBoolean(GameRule.FALL_DAMAGE)) {
                this.attack(EntityDamageEvent(this, DamageCause.FALL, damage))
            }
        }
        if (fallDistance > 0.75) {
            if (down.getId() === Block.FARMLAND) {
                if (onPhysicalInteraction(down, false)) {
                    return
                }
                this.level.setBlock(down, BlockDirt(), false, true)
                return
            }
            val floor: Block = this.level.getBlock(floorLocation)
            if (floor is BlockTurtleEgg) {
                if (onPhysicalInteraction(floor, ThreadLocalRandom.current().nextInt(10) >= 3)) {
                    return
                }
                this.level.useBreakOn(this, null, null, true)
            }
        }
    }

    private fun onPhysicalInteraction(block: Block, cancelled: Boolean): Boolean {
        val ev: Event
        if (this is Player) {
            ev = PlayerInteractEvent(this as Player, null, block, null, Action.PHYSICAL)
        } else {
            ev = EntityInteractEvent(this, block)
        }
        ev.setCancelled(cancelled)
        server.getPluginManager().callEvent(ev)
        return ev.isCancelled()
    }

    fun handleLavaMovement() {
        //todo
    }

    fun moveFlying(strafe: Float, forward: Float, friction: Float) {
        // This is special for Nukkit! :)
        var strafe = strafe
        var forward = forward
        var speed = strafe * strafe + forward * forward
        if (speed >= 1.0E-4f) {
            speed = MathHelper.sqrt(speed)
            if (speed < 1.0f) {
                speed = 1.0f
            }
            speed = friction / speed
            strafe *= speed
            forward *= speed
            val nest: Float = MathHelper.sin((this.yaw * 3.1415927f / 180.0f) as Float)
            val place: Float = MathHelper.cos((this.yaw * 3.1415927f / 180.0f) as Float)
            motionX += (strafe * place - forward * nest).toDouble()
            motionZ += (forward * place + strafe * nest).toDouble()
        }
    }

    fun onCollideWithPlayer(entityPlayer: EntityHuman?) {}
    fun applyEntityCollision(entity: Entity) {
        if (entity.riding !== this && !entity.passengers.contains(this)) {
            var dx: Double = entity.x - this.x
            var dy: Double = entity.z - this.z
            var dz: Double = NukkitMath.getDirection(dx, dy)
            if (dz >= 0.009999999776482582) {
                dz = MathHelper.sqrt(dz.toFloat())
                dx /= dz
                dy /= dz
                var d3 = 1.0 / dz
                if (d3 > 1.0) {
                    d3 = 1.0
                }
                dx *= d3
                dy *= d3
                dx *= 0.05000000074505806
                dy *= 0.05000000074505806
                dx *= 1f + entityCollisionReduction
                if (riding == null) {
                    motionX -= dx
                    motionZ -= dy
                }
            }
        }
    }

    fun onStruckByLightning(entity: Entity?) {
        if (this.attack(EntityDamageByEntityEvent(entity, this, DamageCause.LIGHTNING, 5))) {
            if (fireTicks < 8 * 20) {
                setOnFire(8)
            }
        }
    }

    fun onPushByPiston(piston: BlockEntityPistonArm?) {}
    fun onInteract(player: Player?, item: Item?, clickedPos: Vector3?): Boolean {
        return onInteract(player, item)
    }

    fun onInteract(player: Player?, item: Item?): Boolean {
        return false
    }

    protected fun switchLevel(targetLevel: Level?): Boolean {
        if (isClosed) {
            return false
        }
        if (this.isValid()) {
            val ev = EntityLevelChangeEvent(this, this.level, targetLevel)
            server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            this.level.removeEntity(this)
            if (chunk != null) {
                chunk.removeEntity(this)
            }
            despawnFromAll()
        }
        this.setLevel(targetLevel)
        this.level.addEntity(this)
        chunk = null
        return true
    }

    val position: Position
        get() = Position(this.x, this.y, this.z, this.level)

    @get:Nonnull
    val location: Location
        get() = Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level)
    val isTouchingWater: Boolean
        get() = hasWaterAt(0f) || hasWaterAt(eyeHeight)
    val isInsideOfWater: Boolean
        get() = hasWaterAt(eyeHeight)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun hasWaterAt(height: Float): Boolean {
        val y: Double = this.y + height
        val block: Block = this.level.getBlock(temporalVector.setComponents(NukkitMath.floorDouble(this.x), NukkitMath.floorDouble(y), NukkitMath.floorDouble(this.z)))
        var layer1 = false
        if (block !is BlockBubbleColumn && (block is BlockWater
                        || (block.getLevelBlockAtLayer(1) is BlockWater.also{ layer1 = it }))){
            val water: BlockWater = (if (layer1) block.getLevelBlockAtLayer(1) else block) as BlockWater
            val f: Double = block.y + 1 - (water.getFluidHeightPercent() - 0.1111111)
            return y < f
        }
        return false
    }

    val isInsideOfSolid: Boolean
        get() {
            val y: Double = this.y + eyeHeight
            val block: Block = this.level.getBlock(
                    temporalVector.setComponents(
                            NukkitMath.floorDouble(this.x),
                            NukkitMath.floorDouble(y),
                            NukkitMath.floorDouble(this.z))
            )
            val bb: AxisAlignedBB = block.getBoundingBox()
            return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(getBoundingBox())
        }
    val isInsideOfFire: Boolean
        get() {
            for (block in getCollisionBlocks()!!) {
                if (block is BlockFire) {
                    return true
                }
            }
            return false
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Block?> collideWithBlock(classType: Class<T>): Boolean {
        for (block in getCollisionBlocks()!!) {
            if (classType.isInstance(block)) {
                return true
            }
        }
        return false
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isInsideOfLava: Boolean
        get() {
            for (block in getCollisionBlocks()!!) {
                if (block is BlockLava) {
                    return true
                }
            }
            return false
        }
    val isOnLadder: Boolean
        get() {
            val b: Block = this.getLevelBlock()
            return b.getId() === Block.LADDER
        }

    fun fastMove(dx: Double, dy: Double, dz: Double): Boolean {
        if (dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return true
        }
        Timings.entityMoveTimer.startTiming()
        val newBB: AxisAlignedBB = boundingBox.getOffsetBoundingBox(dx, dy, dz)
        if (server.getAllowFlight()
                || isPlayer && (this as Player).getAdventureSettings().get(AdventureSettings.Type.NO_CLIP) || !this.level.hasCollision(this, newBB, false)) {
            boundingBox = newBB
        }
        this.x = (boundingBox.getMinX() + boundingBox.getMaxX()) / 2
        this.y = boundingBox.getMinY() - ySize
        this.z = (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2
        checkChunks()
        if ((!isOnGround || dy != 0.0) && !noClip) {
            val bb: AxisAlignedBB = boundingBox.clone()
            bb.setMinY(bb.getMinY() - 0.75)
            isOnGround = this.level.getCollisionBlocks(bb).length > 0
        }
        isCollided = isOnGround
        updateFallState(isOnGround)
        Timings.entityMoveTimer.stopTiming()
        return true
    }

    fun move(dx: Double, dy: Double, dz: Double): Boolean {
        var dx = dx
        var dy = dy
        var dz = dz
        if (dx == 0.0 && dz == 0.0 && dy == 0.0) {
            return true
        }
        return if (keepMovement) {
            boundingBox.offset(dx, dy, dz)
            setPosition(temporalVector.setComponents((boundingBox.getMinX() + boundingBox.getMaxX()) / 2, boundingBox.getMinY(), (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2))
            isOnGround = isPlayer
            true
        } else {
            Timings.entityMoveTimer.startTiming()
            ySize *= 0.4f
            val movX = dx
            val movY = dy
            val movZ = dz
            val axisalignedbb: AxisAlignedBB = boundingBox.clone()
            var list: Array<AxisAlignedBB> = if (noClip) AxisAlignedBB.EMPTY_ARRAY else this.level.getCollisionCubes(this, boundingBox.addCoord(dx, dy, dz), false)
            for (bb in list) {
                dy = bb.calculateYOffset(boundingBox, dy)
            }
            boundingBox.offset(0, dy, 0)
            val fallingFlag = isOnGround || dy != movY && movY < 0
            for (bb in list) {
                dx = bb.calculateXOffset(boundingBox, dx)
            }
            boundingBox.offset(dx, 0, 0)
            for (bb in list) {
                dz = bb.calculateZOffset(boundingBox, dz)
            }
            boundingBox.offset(0, 0, dz)
            if (stepHeight > 0 && fallingFlag && ySize < 0.05 && (movX != dx || movZ != dz)) {
                val cx = dx
                val cy = dy
                val cz = dz
                dx = movX
                dy = stepHeight
                dz = movZ
                val axisalignedbb1: AxisAlignedBB = boundingBox.clone()
                boundingBox.setBB(axisalignedbb)
                list = this.level.getCollisionCubes(this, boundingBox.addCoord(dx, dy, dz), false)
                for (bb in list) {
                    dy = bb.calculateYOffset(boundingBox, dy)
                }
                boundingBox.offset(0, dy, 0)
                for (bb in list) {
                    dx = bb.calculateXOffset(boundingBox, dx)
                }
                boundingBox.offset(dx, 0, 0)
                for (bb in list) {
                    dz = bb.calculateZOffset(boundingBox, dz)
                }
                boundingBox.offset(0, 0, dz)
                boundingBox.offset(0, 0, dz)
                if (cx * cx + cz * cz >= dx * dx + dz * dz) {
                    dx = cx
                    dy = cy
                    dz = cz
                    boundingBox.setBB(axisalignedbb1)
                } else {
                    ySize += 0.5f
                }
            }
            this.x = (boundingBox.getMinX() + boundingBox.getMaxX()) / 2
            this.y = boundingBox.getMinY() - ySize
            this.z = (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2
            checkChunks()
            checkGroundState(movX, movY, movZ, dx, dy, dz)
            updateFallState(isOnGround)
            if (movX != dx) {
                motionX = 0.0
            }
            if (movY != dy) {
                motionY = 0.0
            }
            if (movZ != dz) {
                motionZ = 0.0
            }

            //TODO: vehicle collision events (first we need to spawn them!)
            Timings.entityMoveTimer.stopTiming()
            true
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will do nothing if the entity is on ground and all args are 0")
    protected fun checkGroundState(movX: Double, movY: Double, movZ: Double, dx: Double, dy: Double, dz: Double) {
        if (isOnGround && movX == 0.0 && movY == 0.0 && movZ == 0.0 && dx == 0.0 && dy == 0.0 && dz == 0.0) {
            return
        }
        if (noClip) {
            isCollidedVertically = false
            isCollidedHorizontally = false
            isCollided = false
            isOnGround = false
        } else {
            isCollidedVertically = movY != dy
            isCollidedHorizontally = movX != dx || movZ != dz
            isCollided = isCollidedHorizontally || isCollidedVertically
            isOnGround = movY != dy && movY < 0
        }
    }

    fun getBlocksAround(): List<Block>? {
        if (blocksAround == null) {
            val minX: Int = NukkitMath.floorDouble(boundingBox.getMinX())
            val minY: Int = NukkitMath.floorDouble(boundingBox.getMinY())
            val minZ: Int = NukkitMath.floorDouble(boundingBox.getMinZ())
            val maxX: Int = NukkitMath.ceilDouble(boundingBox.getMaxX())
            val maxY: Int = NukkitMath.ceilDouble(boundingBox.getMaxY())
            val maxZ: Int = NukkitMath.ceilDouble(boundingBox.getMaxZ())
            blocksAround = ArrayList()
            for (z in minZ..maxZ) {
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        val block: Block = this.level.getBlock(temporalVector.setComponents(x, y, z))
                        blocksAround.add(block)
                    }
                }
            }
        }
        return blocksAround
    }

    fun getCollisionBlocks(): List<Block>? {
        if (collisionBlocks == null) {
            collisionBlocks = ArrayList()
            for (b in getBlocksAround()!!) {
                if (b.collidesWithBB(getBoundingBox(), true)) {
                    collisionBlocks.add(b)
                }
            }
        }
        return collisionBlocks
    }

    /**
     * Returns whether this entity can be moved by currents in liquids.
     *
     * @return boolean
     */
    fun canBeMovedByCurrents(): Boolean {
        return true
    }

    protected fun checkBlockCollision() {
        if (noClip) {
            return
        }
        var vector = Vector3(0, 0, 0)
        var portal = false
        var scaffolding = false
        var endPortal = false
        for (block in getCollisionBlocks()!!) {
            when (block.getId()) {
                Block.NETHER_PORTAL -> portal = true
                BlockID.SCAFFOLDING -> scaffolding = true
                BlockID.END_PORTAL -> endPortal = true
            }
            block.onEntityCollide(this)
            block.getLevelBlockAtLayer(1).onEntityCollide(this)
            block.addVelocityToEntity(this, vector)
        }
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, scaffolding)
        val scanBoundingBox: AxisAlignedBB = boundingBox.getOffsetBoundingBox(0, -0.125, 0)
        scanBoundingBox.setMaxY(boundingBox.getMinY())
        val scaffoldingUnder: Array<Block> = level.getCollisionBlocks(
                scanBoundingBox,
                true, true
        ) { b -> b.getId() === BlockID.SCAFFOLDING }
        setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, scaffoldingUnder.size > 0)
        if (endPortal) {
            if (!isInEndPortal) {
                isInEndPortal = true
                val ev = EntityPortalEnterEvent(this, PortalType.END)
                getServer().getPluginManager().callEvent(ev)
            }
        } else {
            isInEndPortal = false
        }
        if (portal) {
            if (inPortalTicks <= 80) {
                // 81 means the server won't try to teleport
                inPortalTicks++
            }
        } else {
            inPortalTicks = 0
        }
        if (vector.lengthSquared() > 0) {
            vector = vector.normalize()
            val d = 0.014
            motionX += vector.x * d
            motionY += vector.y * d
            motionZ += vector.z * d
        }
    }

    fun setPositionAndRotation(pos: Vector3, yaw: Double, pitch: Double): Boolean {
        if (setPosition(pos)) {
            setRotation(yaw, pitch)
            return true
        }
        return false
    }

    fun setRotation(yaw: Double, pitch: Double) {
        yaw = yaw
        pitch = pitch
        scheduleUpdate()
    }

    /**
     * Whether the entity can active pressure plates.
     * Used for [cn.nukkit.entity.passive.EntityBat]s only.
     *
     * @return triggers pressure plate
     */
    fun doesTriggerPressurePlate(): Boolean {
        return true
    }

    fun canPassThrough(): Boolean {
        return true
    }

    protected fun checkChunks() {
        if (chunk == null || chunk.getX() !== this.x as Int shr 4 || chunk.getZ() !== this.z as Int shr 4) {
            if (chunk != null) {
                chunk.removeEntity(this)
            }
            chunk = this.level.getChunk(this.x as Int shr 4, this.z as Int shr 4, true)
            if (!justCreated) {
                val newChunk: Map<Integer, Player> = this.level.getChunkPlayers(this.x as Int shr 4, this.z as Int shr 4)
                for (player in ArrayList(hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        despawnFrom(player)
                    } else {
                        newChunk.remove(player.getLoaderId())
                    }
                }
                for (player in newChunk.values()) {
                    spawnTo(player)
                }
            }
            if (chunk == null) {
                return
            }
            chunk.addEntity(this)
        }
    }

    fun setPosition(pos: Vector3): Boolean {
        if (isClosed) {
            return false
        }
        if (pos is Position && (pos as Position).level != null && (pos as Position).level !== this.level) {
            if (!switchLevel((pos as Position).getLevel())) {
                return false
            }
        }
        this.x = pos.x
        this.y = pos.y
        this.z = pos.z
        recalculateBoundingBox(false) // Don't need to send BB height/width to client on position change
        checkChunks()
        return true
    }

    val motion: Vector3
        get() = Vector3(motionX, motionY, motionZ)

    fun setMotion(motion: Vector3): Boolean {
        if (!justCreated) {
            val ev = EntityMotionEvent(this, motion)
            server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
        }
        motionX = motion.x
        motionY = motion.y
        motionZ = motion.z
        if (!justCreated) {
            updateMovement()
        }
        return true
    }

    fun kill() {
        health = 0f
        scheduleUpdate()
        for (passenger in ArrayList(passengers)) {
            dismountEntity(passenger)
        }
    }

    fun teleport(pos: Vector3?): Boolean {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    fun teleport(pos: Vector3?, cause: PlayerTeleportEvent.TeleportCause?): Boolean {
        return this.teleport(Location.fromObject(pos, this.level, this.yaw, this.pitch), cause)
    }

    fun teleport(pos: Position?): Boolean {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    fun teleport(pos: Position, cause: PlayerTeleportEvent.TeleportCause?): Boolean {
        return this.teleport(Location.fromObject(pos, pos.level, this.yaw, this.pitch), cause)
    }

    fun teleport(location: Location?): Boolean {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    fun teleport(location: Location, cause: PlayerTeleportEvent.TeleportCause?): Boolean {
        val yaw: Double = location.yaw
        val pitch: Double = location.pitch
        val from: Location = this.location
        var to: Location = location
        if (cause != null) {
            val ev = EntityTeleportEvent(this, from, to)
            server.getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            to = ev.getTo()
        }
        ySize = 0f
        setMotion(temporalVector.setComponents(0, 0, 0))
        if (setPositionAndRotation(to, yaw, pitch)) {
            resetFallDistance()
            isOnGround = if (noClip) false else true
            updateMovement()
            return true
        }
        return false
    }

    fun respawnToAll() {
        for (player in hasSpawned.values()) {
            spawnTo(player)
        }
        hasSpawned.clear()
    }

    fun spawnToAll() {
        if (chunk == null || isClosed) {
            return
        }
        for (player in this.level.getChunkPlayers(chunk.getX(), chunk.getZ()).values()) {
            if (player.isOnline()) {
                spawnTo(player)
            }
        }
    }

    fun despawnFromAll() {
        for (player in ArrayList(hasSpawned.values())) {
            despawnFrom(player)
        }
    }

    fun close() {
        if (!isClosed) {
            isClosed = true
            try {
                val event = EntityDespawnEvent(this)
                server.getPluginManager().callEvent(event)
                if (event.isCancelled()) {
                    isClosed = false
                    return
                }
            } catch (e: Throwable) {
                isClosed = false
                throw e
            }
            try {
                despawnFromAll()
            } finally {
                try {
                    if (chunk != null) {
                        chunk.removeEntity(this)
                    }
                } finally {
                    if (this.level != null) {
                        this.level.removeEntity(this)
                    }
                }
            }
        }
    }

    private fun close(despawn: Boolean) {
        if (!isClosed) {
            isClosed = true
            if (despawn) {
                val event = EntityDespawnEvent(this)
                server.getPluginManager().callEvent(event)
                if (event.isCancelled()) return
            }
            despawnFromAll()
            if (chunk != null) {
                chunk.removeEntity(this)
            }
            if (this.level != null) {
                this.level.removeEntity(this)
            }
        }
    }

    fun setDataProperty(data: EntityData): Boolean {
        return setDataProperty(data, true)
    }

    fun setDataProperty(data: EntityData, send: Boolean): Boolean {
        if (!Objects.equals(data, getDataProperties().get(data.getId()))) {
            getDataProperties().put(data)
            if (send) {
                this.sendData(hasSpawned.values().toArray(Player.EMPTY_ARRAY), EntityMetadata().put(dataProperties.get(data.getId())))
            }
            return true
        }
        return false
    }

    fun getDataProperties(): EntityMetadata {
        return dataProperties
    }

    fun getDataProperty(id: Int): EntityData {
        return getDataProperties().get(id)
    }

    fun getDataPropertyInt(id: Int): Int {
        return getDataProperties().getInt(id)
    }

    fun getDataPropertyShort(id: Int): Int {
        return getDataProperties().getShort(id)
    }

    fun getDataPropertyByte(id: Int): Int {
        return getDataProperties().getByte(id)
    }

    fun getDataPropertyBoolean(id: Int): Boolean {
        return getDataProperties().getBoolean(id)
    }

    fun getDataPropertyLong(id: Int): Long {
        return getDataProperties().getLong(id)
    }

    fun getDataPropertyString(id: Int): String {
        return getDataProperties().getString(id)
    }

    fun getDataPropertyFloat(id: Int): Float {
        return getDataProperties().getFloat(id)
    }

    fun getDataPropertyNBT(id: Int): CompoundTag {
        return getDataProperties().getNBT(id)
    }

    fun getDataPropertyPos(id: Int): Vector3 {
        return getDataProperties().getPosition(id)
    }

    fun getDataPropertyVector3f(id: Int): Vector3f {
        return getDataProperties().getFloatPosition(id)
    }

    fun getDataPropertyType(id: Int): Int {
        return if (getDataProperties().exists(id)) getDataProperty(id).getType() else -1
    }

    fun setDataFlag(propertyId: Int, id: Int) {
        this.setDataFlag(propertyId, id, true)
    }

    fun setDataFlag(propertyId: Int, id: Int, value: Boolean) {
        if (getDataFlag(propertyId, id) != value) {
            if (propertyId == EntityHuman.DATA_PLAYER_FLAGS) {
                var flags = getDataPropertyByte(propertyId).toByte()
                flags = flags xor (1 shl id)
                this.setDataProperty(ByteEntityData(propertyId, flags))
            } else {
                var flags = getDataPropertyLong(propertyId)
                flags = flags xor (1L shl id)
                this.setDataProperty(LongEntityData(propertyId, flags))
            }
        }
    }

    fun getDataFlag(propertyId: Int, id: Int): Boolean {
        return (if (propertyId == EntityHuman.DATA_PLAYER_FLAGS) getDataPropertyByte(propertyId) and 0xff else getDataPropertyLong(propertyId)) and (1L shl id) > 0
    }

    @Override
    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue)
    }

    @Override
    fun getMetadata(metadataKey: String?): List<MetadataValue> {
        return server.getEntityMetadata().getMetadata(this, metadataKey)
    }

    @Override
    fun hasMetadata(metadataKey: String?): Boolean {
        return server.getEntityMetadata().hasMetadata(this, metadataKey)
    }

    @Override
    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin)
    }

    fun getServer(): Server? {
        return server
    }

    val isUndead: Boolean
        get() = false

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isPreventingSleep(player: Player?): Boolean {
        return false
    }

    @Override
    override fun equals(obj: Object?): Boolean {
        if (obj == null) {
            return false
        }
        if (getClass() !== obj.getClass()) {
            return false
        }
        val other = obj as Entity
        return id == other.id
    }

    @Override
    override fun hashCode(): Int {
        var hash = 7
        hash = (29 * hash + id).toInt()
        return hash
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isSpinAttacking: Boolean
        get() = getDataFlag(DATA_FLAGS, DATA_FLAG_SPIN_ATTACK)
        set(value) {
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_SPIN_ATTACK, value)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setSpinAttacking() {
        isSpinAttacking = true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isNoClip(): Boolean {
        return noClip
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setNoClip(noClip: Boolean) {
        this.noClip = noClip
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_HAS_COLLISION, noClip)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<Entity>(0)
        const val NETWORK_ID = -1
        const val DATA_TYPE_BYTE = 0
        const val DATA_TYPE_SHORT = 1
        const val DATA_TYPE_INT = 2
        const val DATA_TYPE_FLOAT = 3
        const val DATA_TYPE_STRING = 4
        const val DATA_TYPE_NBT = 5
        const val DATA_TYPE_POS = 6
        const val DATA_TYPE_LONG = 7
        const val DATA_TYPE_VECTOR3F = 8
        val DATA_FLAGS: Int = dynamic(0)
        val DATA_HEALTH: Int = dynamic(1) //int (minecart/boat)
        val DATA_VARIANT: Int = dynamic(2) //int
        val DATA_COLOR: Int = dynamic(3) //byte
        val DATA_COLOUR = DATA_COLOR
        val DATA_NAMETAG: Int = dynamic(4) //string
        val DATA_OWNER_EID: Int = dynamic(5) //long
        val DATA_TARGET_EID: Int = dynamic(6) //long
        val DATA_AIR: Int = dynamic(7) //short
        val DATA_POTION_COLOR: Int = dynamic(8) //int (ARGB!)
        val DATA_POTION_AMBIENT: Int = dynamic(9) //byte
        val DATA_JUMP_DURATION: Int = dynamic(10) //long
        val DATA_HURT_TIME: Int = dynamic(11) //int (minecart/boat)
        val DATA_HURT_DIRECTION: Int = dynamic(12) //int (minecart/boat)
        val DATA_PADDLE_TIME_LEFT: Int = dynamic(13) //float
        val DATA_PADDLE_TIME_RIGHT: Int = dynamic(14) //float
        val DATA_EXPERIENCE_VALUE: Int = dynamic(15) //int (xp orb)
        val DATA_DISPLAY_ITEM: Int = dynamic(16) //int (id | (data << 16))
        val DATA_DISPLAY_OFFSET: Int = dynamic(17) //int
        val DATA_HAS_DISPLAY: Int = dynamic(18) //byte (must be 1 for minecart to show block inside)

        @Since("1.2.0.0-PN")
        val DATA_SWELL: Int = dynamic(19)

        @Since("1.2.0.0-PN")
        val DATA_OLD_SWELL: Int = dynamic(20)

        @Since("1.2.0.0-PN")
        val DATA_SWELL_DIR: Int = dynamic(21)

        @Since("1.2.0.0-PN")
        val DATA_CHARGE_AMOUNT: Int = dynamic(22)
        val DATA_ENDERMAN_HELD_RUNTIME_ID: Int = dynamic(23) //short

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DATA_CLIENT_EVENT: Int = dynamic(24) //byte

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Apparently this the ID 24 was reused to represent CLIENT_EVENT but Cloudburst Nukkit is still mapping it as age")
        val DATA_ENTITY_AGE: Int = dynamic(DATA_CLIENT_EVENT) //short

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DATA_USING_ITEM: Int = dynamic(25) //byte
        val DATA_PLAYER_FLAGS: Int = dynamic(26) //byte

        @Since("1.2.0.0-PN")
        val DATA_PLAYER_INDEX: Int = dynamic(27)
        val DATA_PLAYER_BED_POSITION: Int = dynamic(28) //block coords
        val DATA_FIREBALL_POWER_X: Int = dynamic(29) //float
        val DATA_FIREBALL_POWER_Y: Int = dynamic(30) //float
        val DATA_FIREBALL_POWER_Z: Int = dynamic(31) //float

        @Since("1.2.0.0-PN")
        val DATA_AUX_POWER: Int = dynamic(32) //???

        @Since("1.2.0.0-PN")
        val DATA_FISH_X: Int = dynamic(33) //float

        @Since("1.2.0.0-PN")
        val DATA_FISH_Z: Int = dynamic(34) //float

        @Since("1.2.0.0-PN")
        val DATA_FISH_ANGLE: Int = dynamic(35) //float
        val DATA_POTION_AUX_VALUE: Int = dynamic(36) //short
        val DATA_LEAD_HOLDER_EID: Int = dynamic(37) //long
        val DATA_SCALE: Int = dynamic(38) //float

        @Since("1.4.0.0-PN")
        val DATA_INTERACTIVE_TAG: Int = dynamic(39) //string (button text)

        @PowerNukkitOnly
        @Since("1.2.0.0-PN")
        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "This is not only for NPC, it's used to display any interactive button text " +
                "and Nukkit added this constant with a different name", replaceWith = "DATA_INTERACTIVE_TAG")
        val DATA_HAS_NPC_COMPONENT: Int = dynamic(DATA_INTERACTIVE_TAG) //byte
        val DATA_NPC_SKIN_ID: Int = dynamic(40) //string
        val DATA_URL_TAG: Int = dynamic(41) //string
        val DATA_MAX_AIR: Int = dynamic(42) //short
        val DATA_MARK_VARIANT: Int = dynamic(43) //int
        val DATA_CONTAINER_TYPE: Int = dynamic(44) //byte
        val DATA_CONTAINER_BASE_SIZE: Int = dynamic(45) //int
        val DATA_CONTAINER_EXTRA_SLOTS_PER_STRENGTH: Int = dynamic(46) //int
        val DATA_BLOCK_TARGET: Int = dynamic(47) //block coords (ender crystal)
        val DATA_WITHER_INVULNERABLE_TICKS: Int = dynamic(48) //int
        val DATA_WITHER_TARGET_1: Int = dynamic(49) //long
        val DATA_WITHER_TARGET_2: Int = dynamic(50) //long
        val DATA_WITHER_TARGET_3: Int = dynamic(51) //long

        @Since("1.2.0.0-PN")
        val DATA_AERIAL_ATTACK: Int = dynamic(52)
        val DATA_BOUNDING_BOX_WIDTH: Int = dynamic(53) //float
        val DATA_BOUNDING_BOX_HEIGHT: Int = dynamic(54) //float
        val DATA_FUSE_LENGTH: Int = dynamic(55) //int
        val DATA_RIDER_SEAT_POSITION: Int = dynamic(56) //vector3f
        val DATA_RIDER_ROTATION_LOCKED: Int = dynamic(57) //byte
        val DATA_RIDER_MAX_ROTATION: Int = dynamic(58) //float
        val DATA_RIDER_MIN_ROTATION: Int = dynamic(59) //float

        @Since("1.4.0.0-PN")
        val DATA_RIDER_ROTATION_OFFSET: Int = dynamic(60)
        val DATA_AREA_EFFECT_CLOUD_RADIUS: Int = dynamic(61) //float
        val DATA_AREA_EFFECT_CLOUD_WAITING: Int = dynamic(62) //int
        val DATA_AREA_EFFECT_CLOUD_PARTICLE_ID: Int = dynamic(63) //int

        @Since("1.2.0.0-PN")
        val DATA_SHULKER_PEEK_ID: Int = dynamic(64) //int
        val DATA_SHULKER_ATTACH_FACE: Int = dynamic(65) //byte

        @Since("1.2.0.0-PN")
        val DATA_SHULKER_ATTACHED: Int = dynamic(66) //short
        val DATA_SHULKER_ATTACH_POS: Int = dynamic(67) //block coords
        val DATA_TRADING_PLAYER_EID: Int = dynamic(68) //long

        @Since("1.2.0.0-PN")
        val DATA_TRADING_CAREER: Int = dynamic(69)

        @Since("1.2.0.0-PN")
        val DATA_HAS_COMMAND_BLOCK: Int = dynamic(70) //byte

        @Since("1.2.0.0-PN")
        val DATA_COMMAND_BLOCK_COMMAND: Int = dynamic(71) //string
        val DATA_COMMAND_BLOCK_LAST_OUTPUT: Int = dynamic(72) //string
        val DATA_COMMAND_BLOCK_TRACK_OUTPUT: Int = dynamic(73) //byte
        val DATA_CONTROLLING_RIDER_SEAT_NUMBER: Int = dynamic(74) //byte
        val DATA_STRENGTH: Int = dynamic(75) //int
        val DATA_MAX_STRENGTH: Int = dynamic(76) //int

        @Since("1.2.0.0-PN")
        val DATA_SPELL_CASTING_COLOR: Int = dynamic(77) //int
        val DATA_LIMITED_LIFE: Int = dynamic(78) //int
        val DATA_ARMOR_STAND_POSE_INDEX: Int = dynamic(79) //int
        val DATA_ENDER_CRYSTAL_TIME_OFFSET: Int = dynamic(80) //int
        val DATA_ALWAYS_SHOW_NAMETAG: Int = dynamic(81) //byte
        val DATA_COLOR_2: Int = dynamic(82) //byte

        @Since("1.2.0.0-PN")
        val DATA_NAME_AUTHOR: Int = dynamic(83)
        val DATA_SCORE_TAG: Int = dynamic(84) //String
        val DATA_BALLOON_ATTACHED_ENTITY: Int = dynamic(85) //long
        val DATA_PUFFERFISH_SIZE: Int = dynamic(86) //byte

        @Since("1.2.0.0-PN")
        val DATA_BUBBLE_TIME: Int = dynamic(87) //int

        @Since("1.2.0.0-PN")
        val DATA_AGENT: Int = dynamic(88) //long

        @Since("1.2.0.0-PN")
        val DATA_SITTING_AMOUNT: Int = dynamic(89) //??

        @Since("1.2.0.0-PN")
        val DATA_SITTING_AMOUNT_PREVIOUS: Int = dynamic(90) //??

        @Since("1.2.0.0-PN")
        val DATA_EATING_COUNTER: Int = dynamic(91) //int
        val DATA_FLAGS_EXTENDED: Int = dynamic(92) //flags

        @Since("1.2.0.0-PN")
        val DATA_LAYING_AMOUNT: Int = dynamic(93) //??

        @Since("1.2.0.0-PN")
        val DATA_LAYING_AMOUNT_PREVIOUS: Int = dynamic(94) //??

        @Since("1.2.0.0-PN")
        val DATA_DURATION: Int = dynamic(95) //int

        @Since("1.2.0.0-PN")
        val DATA_SPAWN_TIME: Int = dynamic(96) //int

        @Since("1.2.0.0-PN")
        val DATA_CHANGE_RATE: Int = dynamic(97) //float

        @Since("1.2.0.0-PN")
        val DATA_CHANGE_ON_PICKUP: Int = dynamic(98) //float

        @Since("1.2.0.0-PN")
        val DATA_PICKUP_COUNT: Int = dynamic(99) //int

        @Since("1.2.0.0-PN")
        val DATA_INTERACT_TEXT: Int = dynamic(100) //string
        val DATA_TRADE_TIER: Int = dynamic(101) //int
        val DATA_MAX_TRADE_TIER: Int = dynamic(102) //int

        @Since("1.2.0.0-PN")
        val DATA_TRADE_EXPERIENCE: Int = dynamic(103) //int

        @Since("1.1.1.0-PN")
        val DATA_SKIN_ID: Int = dynamic(104) //int

        @Since("1.2.0.0-PN")
        val DATA_SPAWNING_FRAMES: Int = dynamic(105) //??

        @Since("1.2.0.0-PN")
        val DATA_COMMAND_BLOCK_TICK_DELAY: Int = dynamic(106) //int

        @Since("1.2.0.0-PN")
        val DATA_COMMAND_BLOCK_EXECUTE_ON_FIRST_TICK: Int = dynamic(107) //byte

        @Since("1.2.0.0-PN")
        val DATA_AMBIENT_SOUND_INTERVAL: Int = dynamic(108) //float

        @Since("1.3.0.0-PN")
        val DATA_AMBIENT_SOUND_INTERVAL_RANGE: Int = dynamic(109) //float

        @Since("1.2.0.0-PN")
        val DATA_AMBIENT_SOUND_EVENT_NAME: Int = dynamic(110) //string

        @Since("1.2.0.0-PN")
        val DATA_FALL_DAMAGE_MULTIPLIER: Int = dynamic(111) //float

        @Since("1.2.0.0-PN")
        val DATA_NAME_RAW_TEXT: Int = dynamic(112) //??

        @Since("1.2.0.0-PN")
        val DATA_CAN_RIDE_TARGET: Int = dynamic(113) //byte

        @Since("1.3.0.0-PN")
        val DATA_LOW_TIER_CURED_DISCOUNT: Int = dynamic(114) //int

        @Since("1.3.0.0-PN")
        val DATA_HIGH_TIER_CURED_DISCOUNT: Int = dynamic(115) //int

        @Since("1.3.0.0-PN")
        val DATA_NEARBY_CURED_DISCOUNT: Int = dynamic(116) //int

        @Since("1.3.0.0-PN")
        val DATA_NEARBY_CURED_DISCOUNT_TIMESTAMP: Int = dynamic(117) //int

        @Since("1.3.0.0-PN")
        val DATA_HITBOX: Int = dynamic(118) //NBT

        @Since("1.3.0.0-PN")
        val DATA_IS_BUOYANT: Int = dynamic(119) //byte

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val DATA_BASE_RUNTIME_ID: Int = dynamic(120) // ???

        @Since("1.4.0.0-PN")
        val DATA_FREEZING_EFFECT_STRENGTH: Int = dynamic(121)

        @Since("1.3.0.0-PN")
        val DATA_BUOYANCY_DATA: Int = dynamic(122) //string

        @Since("1.4.0.0-PN")
        val DATA_GOAT_HORN_COUNT: Int = dynamic(123) // ???

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val DATA_UPDATE_PROPERTIES: Int = dynamic(124) // ???

        // Flags
        val DATA_FLAG_ONFIRE: Int = dynamic(0)
        val DATA_FLAG_SNEAKING: Int = dynamic(1)
        val DATA_FLAG_RIDING: Int = dynamic(2)
        val DATA_FLAG_SPRINTING: Int = dynamic(3)
        val DATA_FLAG_ACTION: Int = dynamic(4)
        val DATA_FLAG_INVISIBLE: Int = dynamic(5)
        val DATA_FLAG_TEMPTED: Int = dynamic(6)
        val DATA_FLAG_INLOVE: Int = dynamic(7)
        val DATA_FLAG_SADDLED: Int = dynamic(8)
        val DATA_FLAG_POWERED: Int = dynamic(9)
        val DATA_FLAG_IGNITED: Int = dynamic(10)
        val DATA_FLAG_BABY: Int = dynamic(11) //disable head scaling
        val DATA_FLAG_CONVERTING: Int = dynamic(12)
        val DATA_FLAG_CRITICAL: Int = dynamic(13)
        val DATA_FLAG_CAN_SHOW_NAMETAG: Int = dynamic(14)
        val DATA_FLAG_ALWAYS_SHOW_NAMETAG: Int = dynamic(15)
        val DATA_FLAG_IMMOBILE: Int = dynamic(16)
        val DATA_FLAG_NO_AI = DATA_FLAG_IMMOBILE
        val DATA_FLAG_SILENT: Int = dynamic(17)
        val DATA_FLAG_WALLCLIMBING: Int = dynamic(18)
        val DATA_FLAG_CAN_CLIMB: Int = dynamic(19)
        val DATA_FLAG_SWIMMER: Int = dynamic(20)
        val DATA_FLAG_CAN_FLY: Int = dynamic(21)
        val DATA_FLAG_WALKER: Int = dynamic(22)
        val DATA_FLAG_RESTING: Int = dynamic(23)
        val DATA_FLAG_SITTING: Int = dynamic(24)
        val DATA_FLAG_ANGRY: Int = dynamic(25)
        val DATA_FLAG_INTERESTED: Int = dynamic(26)
        val DATA_FLAG_CHARGED: Int = dynamic(27)
        val DATA_FLAG_TAMED: Int = dynamic(28)
        val DATA_FLAG_ORPHANED: Int = dynamic(29)
        val DATA_FLAG_LEASHED: Int = dynamic(30)
        val DATA_FLAG_SHEARED: Int = dynamic(31)
        val DATA_FLAG_GLIDING: Int = dynamic(32)
        val DATA_FLAG_ELDER: Int = dynamic(33)
        val DATA_FLAG_MOVING: Int = dynamic(34)
        val DATA_FLAG_BREATHING: Int = dynamic(35)
        val DATA_FLAG_CHESTED: Int = dynamic(36)
        val DATA_FLAG_STACKABLE: Int = dynamic(37)
        val DATA_FLAG_SHOWBASE: Int = dynamic(38)
        val DATA_FLAG_REARING: Int = dynamic(39)
        val DATA_FLAG_VIBRATING: Int = dynamic(40)
        val DATA_FLAG_IDLING: Int = dynamic(41)
        val DATA_FLAG_EVOKER_SPELL: Int = dynamic(42)
        val DATA_FLAG_CHARGE_ATTACK: Int = dynamic(43)
        val DATA_FLAG_WASD_CONTROLLED: Int = dynamic(44)
        val DATA_FLAG_CAN_POWER_JUMP: Int = dynamic(45)
        val DATA_FLAG_LINGER: Int = dynamic(46)
        val DATA_FLAG_HAS_COLLISION: Int = dynamic(47)
        val DATA_FLAG_GRAVITY: Int = dynamic(48)
        val DATA_FLAG_FIRE_IMMUNE: Int = dynamic(49)
        val DATA_FLAG_DANCING: Int = dynamic(50)
        val DATA_FLAG_ENCHANTED: Int = dynamic(51)
        val DATA_FLAG_SHOW_TRIDENT_ROPE: Int = dynamic(52) // tridents show an animated rope when enchanted with loyalty after they are thrown and return to their owner. To be combined with DATA_OWNER_EID
        val DATA_FLAG_CONTAINER_PRIVATE: Int = dynamic(53) //inventory is private, doesn't drop contents when killed if true

        @Since("1.2.0.0-PN")
        val DATA_FLAG_IS_TRANSFORMING: Int = dynamic(54)
        val DATA_FLAG_SPIN_ATTACK: Int = dynamic(55)
        val DATA_FLAG_SWIMMING: Int = dynamic(56)
        val DATA_FLAG_BRIBED: Int = dynamic(57) //dolphins have this set when they go to find treasure for the player
        val DATA_FLAG_PREGNANT: Int = dynamic(58)
        val DATA_FLAG_LAYING_EGG: Int = dynamic(59)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_RIDER_CAN_PICK: Int = dynamic(60)

        @PowerNukkitOnly
        @Since("1.2.0.0-PN")
        val DATA_FLAG_TRANSITION_SITTING: Int = dynamic(61) // PowerNukkit but without typo

        /**
         * @see .DATA_FLAG_TRANSITION_SITTING
         */
        @Deprecated
        @DeprecationDetails(reason = "This is from NukkitX but it has a typo which we can't remove unless NukkitX removes from their side.", since = "1.2.0.0-PN", replaceWith = "DATA_FLAG_TRANSITION_SITTING")
        @Since("1.2.0.0-PN")
        @Deprecated("""This is from NukkitX but it has a typo which we can't remove unless NukkitX removes from their side.
      """)
        val DATA_FLAG_TRANSITION_SETTING = DATA_FLAG_TRANSITION_SITTING // NukkitX with the same typo
        val DATA_FLAG_EATING: Int = dynamic(62)
        val DATA_FLAG_LAYING_DOWN: Int = dynamic(63)
        val DATA_FLAG_SNEEZING: Int = dynamic(64)
        val DATA_FLAG_TRUSTING: Int = dynamic(65)
        val DATA_FLAG_ROLLING: Int = dynamic(66)
        val DATA_FLAG_SCARED: Int = dynamic(67)
        val DATA_FLAG_IN_SCAFFOLDING: Int = dynamic(68)
        val DATA_FLAG_OVER_SCAFFOLDING: Int = dynamic(69)
        val DATA_FLAG_FALL_THROUGH_SCAFFOLDING: Int = dynamic(70)
        val DATA_FLAG_BLOCKING: Int = dynamic(71) //shield

        @Since("1.2.0.0-PN")
        val DATA_FLAG_TRANSITION_BLOCKING: Int = dynamic(72)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_BLOCKED_USING_SHIELD: Int = dynamic(73)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_BLOCKED_USING_DAMAGED_SHIELD: Int = dynamic(74)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_SLEEPING: Int = dynamic(75)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_WANTS_TO_WAKE: Int = dynamic(76)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_TRADE_INTEREST: Int = dynamic(77)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_DOOR_BREAKER: Int = dynamic(78)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_BREAKING_OBSTRUCTION: Int = dynamic(79)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_DOOR_OPENER: Int = dynamic(80)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_IS_ILLAGER_CAPTAIN: Int = dynamic(81)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_STUNNED: Int = dynamic(82)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_ROARING: Int = dynamic(83)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_DELAYED_ATTACK: Int = dynamic(84)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_IS_AVOIDING_MOBS: Int = dynamic(85)

        @Since("1.3.0.0-PN")
        val DATA_FLAG_IS_AVOIDING_BLOCKS: Int = dynamic(86)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_FACING_TARGET_TO_RANGE_ATTACK: Int = dynamic(87)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_HIDDEN_WHEN_INVISIBLE: Int = dynamic(88)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_IS_IN_UI: Int = dynamic(89)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_STALKING: Int = dynamic(90)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_EMOTING: Int = dynamic(91)

        @Since("1.2.0.0-PN")
        val DATA_FLAG_CELEBRATING: Int = dynamic(92)

        @Since("1.3.0.0-PN")
        val DATA_FLAG_ADMIRING: Int = dynamic(93)

        @Since("1.3.0.0-PN")
        val DATA_FLAG_CELEBRATING_SPECIAL: Int = dynamic(94)

        @Since("1.4.0.0-PN")
        val DATA_FLAG_RAM_ATTACK: Int = dynamic(96)

        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val DATA_FLAG_PLAYING_DEAD: Int = dynamic(97)
        var entityCount: Long = 1
        private val knownEntities: Map<String, Class<out Entity>> = HashMap()
        private val shortNames: Map<String, String> = HashMap()
        @Nullable
        fun createEntity(@Nonnull name: String?, @Nonnull pos: Position, @Nullable vararg args: Object?): Entity {
            return createEntity(name, pos.getChunk(), getDefaultNBT(pos), args)
        }

        @Nullable
        fun createEntity(type: Int, @Nonnull pos: Position, @Nullable vararg args: Object?): Entity {
            return createEntity(String.valueOf(type), pos.getChunk(), getDefaultNBT(pos), args)
        }

        @Nullable
        fun createEntity(@Nonnull name: String, @Nonnull chunk: FullChunk?, @Nonnull nbt: CompoundTag?, @Nullable vararg args: Object?): Entity? {
            var entity: Entity? = null
            val clazz: Class<out Entity>? = knownEntities[name]
            if (clazz != null) {
                var exceptions: List<Exception?>? = null
                for (constructor in clazz.getConstructors()) {
                    if (entity != null) {
                        break
                    }
                    if (constructor.getParameterCount() !== (if (args == null) 2 else args.size + 2)) {
                        continue
                    }
                    try {
                        if (args == null || args.size == 0) {
                            entity = constructor.newInstance(chunk, nbt)
                        } else {
                            val objects: Array<Object?> = arrayOfNulls<Object>(args.size + 2)
                            objects[0] = chunk
                            objects[1] = nbt
                            System.arraycopy(args, 0, objects, 2, args.size)
                            entity = constructor.newInstance(objects)
                        }
                    } catch (e: Exception) {
                        if (exceptions == null) {
                            exceptions = ArrayList()
                        }
                        exceptions.add(e)
                    }
                }
                if (entity == null) {
                    val cause: Exception = IllegalArgumentException("Could not create an entity of type $name", if (exceptions != null && exceptions.size() > 0) exceptions[0] else null)
                    if (exceptions != null && exceptions.size() > 1) {
                        for (i in 1 until exceptions.size()) {
                            cause.addSuppressed(exceptions[i])
                        }
                    }
                    log.debug("Could not create an entity of type {} with {} args", name, args?.size ?: 0, cause)
                }
            } else {
                log.debug("Entity type {} is unknown", name)
            }
            return entity
        }

        @Nullable
        fun createEntity(type: Int, @Nonnull chunk: FullChunk?, @Nonnull nbt: CompoundTag?, @Nullable vararg args: Object?): Entity {
            return createEntity(String.valueOf(type), chunk, nbt, args)
        }

        fun registerEntity(name: String?, clazz: Class<out Entity?>?): Boolean {
            return registerEntity(name, clazz, false)
        }

        fun registerEntity(name: String?, clazz: Class<out Entity?>?, force: Boolean): Boolean {
            if (clazz == null) {
                return false
            }
            try {
                val networkId: Int = clazz.getField("NETWORK_ID").getInt(null)
                knownEntities.put(String.valueOf(networkId), clazz)
            } catch (e: Exception) {
                if (!force) {
                    return false
                }
            }
            knownEntities.put(name, clazz)
            shortNames.put(clazz.getSimpleName(), name)
            return true
        }

        @Nonnull
        fun getDefaultNBT(@Nonnull pos: Vector3): CompoundTag {
            return getDefaultNBT(pos, null)
        }

        @Nonnull
        fun getDefaultNBT(@Nonnull pos: Vector3, @Nullable motion: Vector3?): CompoundTag {
            val loc: Location? = if (pos is Location) pos as Location else null
            return if (loc != null) {
                getDefaultNBT(pos, motion, loc.getYaw() as Float, loc.getPitch() as Float)
            } else getDefaultNBT(pos, motion, 0f, 0f)
        }

        @Nonnull
        fun getDefaultNBT(@Nonnull pos: Vector3, @Nullable motion: Vector3?, yaw: Float, pitch: Float): CompoundTag {
            return CompoundTag()
                    .putList(ListTag<DoubleTag>("Pos")
                            .add(DoubleTag("", pos.x))
                            .add(DoubleTag("", pos.y))
                            .add(DoubleTag("", pos.z)))
                    .putList(ListTag<DoubleTag>("Motion")
                            .add(DoubleTag("", if (motion != null) motion.x else 0))
                            .add(DoubleTag("", if (motion != null) motion.y else 0))
                            .add(DoubleTag("", if (motion != null) motion.z else 0)))
                    .putList(ListTag<FloatTag>("Rotation")
                            .add(FloatTag("", yaw))
                            .add(FloatTag("", pitch)))
        }
    }

    init {
        if (this is Player) {
            return
        }
        init(chunk, nbt)
    }
}