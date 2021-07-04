package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author yescallop
 * @since 2016/2/13
 */
class EntityBoat(chunk: FullChunk?, nbt: CompoundTag?) : EntityVehicle(chunk, nbt) {
    protected var sinking = true
    private var ticksInWater = 0
    private val ignoreCollision: Set<Entity> = HashSet(2)

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Unreliable direct field access", replaceWith = "getVariant(), setVariant(int)")
    @Since("1.4.0.0-PN")
    var woodID = 0
    @Override
    protected fun initEntity() {
        super.initEntity()
        if (this.namedTag.contains("Variant")) {
            woodID = this.namedTag.getInt("Variant")
        } else if (this.namedTag.contains("woodID")) {
            woodID = this.namedTag.getByte("woodID")
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_STACKABLE, true)
        this.dataProperties.putInt(DATA_VARIANT, woodID)
        this.dataProperties.putBoolean(DATA_IS_BUOYANT, true)
        this.dataProperties.putString(DATA_BUOYANCY_DATA, "{\"apply_gravity\":true,\"base_buoyancy\":1.0,\"big_wave_probability\":0.02999999932944775,\"big_wave_speed\":10.0,\"drag_down_on_buoyancy_removed\":0.0,\"liquid_blocks\":[\"minecraft:water\",\"minecraft:flowing_water\"],\"simulate_waves\":true}")
        this.dataProperties.putInt(DATA_MAX_AIR, 300)
        this.dataProperties.putLong(DATA_OWNER_EID, -1)
        this.dataProperties.putFloat(DATA_PADDLE_TIME_LEFT, 0)
        this.dataProperties.putFloat(DATA_PADDLE_TIME_RIGHT, 0)
        this.dataProperties.putByte(DATA_CONTROLLING_RIDER_SEAT_NUMBER, 0)
        this.dataProperties.putInt(DATA_LIMITED_LIFE, -1)
        this.dataProperties.putByte(DATA_ALWAYS_SHOW_NAMETAG, -1)
        this.dataProperties.putFloat(DATA_AMBIENT_SOUND_INTERVAL, 8f)
        this.dataProperties.putFloat(DATA_AMBIENT_SOUND_INTERVAL_RANGE, 16f)
        this.dataProperties.putString(DATA_AMBIENT_SOUND_EVENT_NAME, "ambient")
        this.dataProperties.putFloat(DATA_FALL_DAMAGE_MULTIPLIER, 1f)
        entityCollisionReduction = -0.5
    }

    @get:Override
    val height: Float
        get() = 0.455f

    @get:Override
    val width: Float
        get() = 1.4f

    @get:Override
    protected val drag: Float
        protected get() = 0.1f

    @get:Override
    protected val gravity: Float
        protected get() = 0.03999999910593033f

    @get:Override
    val baseOffset: Float
        get() = 0.375f

    @get:Override
    override val interactButtonText: String
        get() = "action.interact.ride.boat"

    @Override
    override fun attack(source: EntityDamageEvent): Boolean {
        return if (invulnerable) {
            false
        } else {
            source.setDamage(source.getDamage() * 2)
            val attack: Boolean = super.attack(source)
            if (isAlive()) {
                performHurtAnimation()
            }
            attack
        }
    }

    @Override
    fun close() {
        super.close()
        for (linkedEntity in this.passengers) {
            linkedEntity.riding = null
        }
    }

    @Override
    protected fun createAddEntityPacket(): DataPacket {
        val addEntity = AddEntityPacket()
        addEntity.type = 0
        addEntity.id = "minecraft:boat"
        addEntity.entityUniqueId = this.getId()
        addEntity.entityRuntimeId = this.getId()
        addEntity.yaw = this.yaw as Float
        addEntity.headYaw = this.yaw as Float
        addEntity.pitch = this.pitch as Float
        addEntity.x = this.x as Float
        addEntity.y = this.y as Float + baseOffset
        addEntity.z = this.z as Float
        addEntity.speedX = this.motionX as Float
        addEntity.speedY = this.motionY as Float
        addEntity.speedZ = this.motionZ as Float
        addEntity.metadata = this.dataProperties
        addEntity.links = arrayOfNulls<EntityLink>(this.passengers.size())
        for (i in 0 until addEntity.links.length) {
            addEntity.links.get(i) = EntityLink(this.getId(), this.passengers.get(i).getId(), if (i == 0) EntityLink.TYPE_RIDER else TYPE_PASSENGER, false, false)
        }
        return addEntity
    }

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
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
            hasUpdate = updateBoat(tickDiff) || hasUpdate
        }
        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001
    }

    private fun updateBoat(tickDiff: Int): Boolean {
        // The rolling amplitude
        if (getRollingAmplitude() > 0) {
            setRollingAmplitude(getRollingAmplitude() - 1)
        }

        // A killer task
        if (y < -16) {
            kill()
            return false
        }
        var hasUpdated = false
        val waterDiff = waterLevel
        if (!hasControllingPassenger()) {
            hasUpdated = computeBuoyancy(waterDiff)
            val iterator: Iterator<Entity> = ignoreCollision.iterator()
            while (iterator.hasNext()) {
                val ignored: Entity = iterator.next()
                if (!ignored.isValid() || ignored.isClosed() || !ignored.isAlive()
                        || !ignored.getBoundingBox().intersectsWith(getBoundingBox().grow(0.5, 0.5, 0.5))) {
                    iterator.remove()
                    hasUpdated = true
                }
            }
            moveBoat(waterDiff)
        } else {
            updateMovement()
        }
        hasUpdated = hasUpdated || positionChanged
        if (waterDiff >= -SINKING_DEPTH) {
            if (ticksInWater != 0) {
                ticksInWater = 0
                hasUpdated = true
            }
            //hasUpdated = collectCollidingEntities() || hasUpdated;
        } else {
            hasUpdated = true
            ticksInWater += tickDiff
            if (ticksInWater >= 3 * 20) {
                for (i in passengers.size() - 1 downTo 0) {
                    dismountEntity(passengers.get(i))
                }
            }
        }
        this.getServer().getPluginManager().callEvent(VehicleUpdateEvent(this))
        return hasUpdated
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return super.canCollideWith(entity) && !isPassenger(entity)
    }

    @Override
    override fun canDoInteraction(): Boolean {
        return passengers.size() < 2
    }

    private fun moveBoat(waterDiff: Double) {
        checkObstruction(this.x, this.y, this.z)
        move(this.motionX, this.motionY, this.motionZ)
        var friction = (1 - drag).toDouble()
        if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
            friction *= this.getLevel().getBlock(this.temporalVector.setComponents(Math.floor(this.x) as Int, Math.floor(this.y - 1) as Int, Math.floor(this.z) as Int - 1)).getFrictionFactor()
        }
        this.motionX *= friction
        if (!onGround || waterDiff > SINKING_DEPTH /* || sinking*/) {
            this.motionY = if (waterDiff > 0.5) this.motionY - gravity else if (this.motionY - SINKING_SPEED < -SINKING_MAX_SPEED) this.motionY else this.motionY - SINKING_SPEED
        }
        this.motionZ *= friction
        val from = Location(lastX, lastY, lastZ, lastYaw, lastPitch, level)
        val to = Location(this.x, this.y, this.z, this.yaw, this.pitch, level)
        if (!from.equals(to)) {
            this.getServer().getPluginManager().callEvent(VehicleMoveEvent(this, from, to))
        }

        //TODO: lily pad collision
        this.updateMovement()
    }

    private fun collectCollidingEntities(): Boolean {
        if (this.passengers.size() >= 2) {
            return false
        }
        for (entity in this.level.getCollidingEntities(this.boundingBox.grow(0.20000000298023224, 0.0, 0.20000000298023224), this)) {
            if (entity.riding != null || entity !is EntityLiving || entity is Player || entity is EntityWaterAnimal || isPassenger(entity)) {
                continue
            }
            this.mountEntity(entity)
            if (this.passengers.size() >= 2) {
                break
            }
        }
        return true
    }

    private fun computeBuoyancy(waterDiff: Double): Boolean {
        var waterDiff = waterDiff
        var hasUpdated = false
        waterDiff -= (baseOffset / 4).toDouble()
        if (waterDiff > SINKING_DEPTH && !sinking) {
            sinking = true
        } else if (waterDiff < -SINKING_DEPTH && sinking) {
            sinking = false
        }
        if (waterDiff < -SINKING_DEPTH / 1.7) {
            this.motionY = Math.min(0.05 / 10, this.motionY + 0.005)
            hasUpdated = true
        } else if (waterDiff < 0 || !sinking) {
            this.motionY = if (this.motionY > SINKING_MAX_SPEED / 2) Math.max(this.motionY - 0.02, SINKING_MAX_SPEED / 2) else this.motionY + SINKING_SPEED
            hasUpdated = true
        }
        return hasUpdated
    }

    @JvmOverloads
    fun updatePassengers(sendLinks: Boolean = false) {
        if (this.passengers.isEmpty()) {
            return
        }
        for (passenger in ArrayList(passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger)
            }
        }
        var ent: Entity
        if (passengers.size() === 1) {
            this.passengers.get(0).also { ent = it }.setSeatPosition(getMountedOffset(ent))
            super.updatePassengerPosition(ent)
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE)
            }
        } else if (passengers.size() === 2) {
            if (passengers.get(0).also { ent = it } !is Player) { //swap
                val passenger2: Entity = passengers.get(1)
                if (passenger2 is Player) {
                    this.passengers.set(0, passenger2)
                    this.passengers.set(1, ent)
                    ent = passenger2
                }
            }
            ent.setSeatPosition(getMountedOffset(ent).add(RIDER_PASSENGER_OFFSET))
            super.updatePassengerPosition(ent)
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_RIDE)
            }
            this.passengers.get(1).also { ent = it }.setSeatPosition(getMountedOffset(ent).add(PASSENGER_OFFSET))
            super.updatePassengerPosition(ent)
            if (sendLinks) {
                broadcastLinkPacket(ent, SetEntityLinkPacket.TYPE_PASSENGER)
            }

            //float yawDiff = ent.getId() % 2 == 0 ? 90 : 270;
            //ent.setRotation(this.yaw + yawDiff, ent.pitch);
            //ent.updateMovement();
        } else {
            for (passenger in passengers) {
                super.updatePassengerPosition(passenger)
            }
        }
    }

    val waterLevel: Double
        get() {
            val maxY: Double = this.boundingBox.getMinY() + baseOffset
            val consumer: AxisAlignedBB.BBConsumer<Double> = object : BBConsumer<Double?>() {
                private var diffY = Double.MAX_VALUE
                @Override
                fun accept(x: Int, y: Int, z: Int) {
                    var block: Block = this@EntityBoat.level.getBlock(this@EntityBoat.temporalVector.setComponents(x, y, z))
                    if (block is BlockWater || block.getLevelBlockAtLayer(1).also { block = it } is BlockWater) {
                        val level: Double = block.getMaxY()
                        diffY = Math.min(maxY - level, diffY)
                    }
                }

                @Override
                fun get(): Double {
                    return diffY
                }
            }
            this.boundingBox.forEach(consumer)
            return consumer.get()
        }

    @Override
    fun mountEntity(entity: Entity?): Boolean {
        val player = this.passengers.size() >= 1 && this.passengers.get(0) is Player
        var mode: Byte = SetEntityLinkPacket.TYPE_PASSENGER
        if (!player && (entity is Player || this.passengers.size() === 0)) {
            mode = SetEntityLinkPacket.TYPE_RIDE
        }
        return super.mountEntity(entity, mode)
    }

    @Override
    fun mountEntity(entity: Entity, mode: Byte): Boolean {
        val r: Boolean = super.mountEntity(entity, mode)
        if (entity.riding === this) {
            updatePassengers(true)
            entity.setDataProperty(ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 1))
            entity.setDataProperty(FloatEntityData(DATA_RIDER_MAX_ROTATION, 90))
            entity.setDataProperty(FloatEntityData(DATA_RIDER_ROTATION_OFFSET, -90))
            entity.setDataProperty(FloatEntityData(DATA_RIDER_MIN_ROTATION, if (this.passengers.indexOf(entity) === 1) -90 else 1))
            entity.setRotation(yaw, entity.pitch)
            entity.updateMovement()
        }
        return r
    }

    @Override
    protected fun updatePassengerPosition(passenger: Entity?) {
        updatePassengers()
    }

    @Override
    fun dismountEntity(entity: Entity): Boolean {
        val r: Boolean = super.dismountEntity(entity)
        updatePassengers()
        entity.setDataProperty(ByteEntityData(DATA_RIDER_ROTATION_LOCKED, 0))
        if (entity is EntityHuman) {
            ignoreCollision.add(entity)
        }
        return r
    }

    @Override
    fun isControlling(entity: Entity?): Boolean {
        return entity is Player && this.passengers.indexOf(entity) === 0
    }

    @Override
    fun onInteract(player: Player?, item: Item?, clickedPos: Vector3?): Boolean {
        if (this.passengers.size() >= 2 || waterLevel < -SINKING_DEPTH) {
            return false
        }
        super.mountEntity(player)
        return super.onInteract(player, item, clickedPos)
    }

    @Override
    fun getMountedOffset(entity: Entity?): Vector3f {
        return if (entity is Player) RIDER_PLAYER_OFFSET else RIDER_OFFSET
    }

    fun onPaddle(animation: AnimatePacket.Action, value: Float) {
        val propertyId: Int = if (animation === AnimatePacket.Action.ROW_RIGHT) DATA_PADDLE_TIME_RIGHT else DATA_PADDLE_TIME_LEFT
        if (Float.compare(getDataPropertyFloat(propertyId), value) !== 0) {
            this.setDataProperty(FloatEntityData(propertyId, value))
        }
    }

    @Override
    fun applyEntityCollision(entity: Entity) {
        if (this.riding == null && !hasControllingPassenger() && entity.riding !== this && !entity.passengers.contains(this) && !ignoreCollision.contains(entity)) {
            if (!entity.boundingBox.intersectsWith(this.boundingBox.grow(0.20000000298023224, -0.1, 0.20000000298023224))
                    || entity is Player && (entity as Player).isSpectator()) {
                return
            }
            var diffX: Double = entity.x - this.x
            var diffZ: Double = entity.z - this.z
            var direction: Double = NukkitMath.getDirection(diffX, diffZ)
            if (direction >= 0.009999999776482582) {
                direction = Math.sqrt(direction)
                diffX /= direction
                diffZ /= direction
                val d3: Double = Math.min(1 / direction, 1)
                diffX *= d3
                diffZ *= d3
                diffX *= 0.05000000074505806
                diffZ *= 0.05000000074505806
                diffX *= 1 + entityCollisionReduction
                diffZ *= 1 + entityCollisionReduction
                if (this.riding == null) {
                    motionX -= diffX
                    motionZ -= diffZ
                }
            }
        }
    }

    @Override
    fun canPassThrough(): Boolean {
        return false
    }

    @PowerNukkitDifference(info = "Fixes a dupe issue when attacking too quickly", since = "1.3.1.2-PN")
    @Override
    fun kill() {
        if (!isAlive()) {
            return
        }
        super.kill()
        if (level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, Item.get(ItemID.BOAT, woodID))
        }
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        this.namedTag.putInt("Variant", woodID) // Correct way in Bedrock Edition
        this.namedTag.putByte("woodID", woodID) // Compatibility with Cloudburst Nukkit
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var variant: Int
        get() = woodID
        set(variant) {
            woodID = variant
            this.dataProperties.putInt(DATA_VARIANT, variant)
        }

    companion object {
        @get:Override
        val networkId = 90
            get() = Companion.field

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "Was removed because it is already defined in Entity.DATA_VARIANT", replaceWith = "Entity.DATA_VARIANT")
        @PowerNukkitOnly
        val DATA_WOOD_ID = 20
        val RIDER_PLAYER_OFFSET: Vector3f = Vector3f(0, 1.02001f, 0)
        val RIDER_OFFSET: Vector3f = Vector3f(0, -0.2f, 0)
        val PASSENGER_OFFSET: Vector3f = Vector3f(-0.6f)
        val RIDER_PASSENGER_OFFSET: Vector3f = Vector3f(0.2f)
        const val RIDER_INDEX = 0
        const val PASSENGER_INDEX = 1
        const val SINKING_DEPTH = 0.07
        const val SINKING_SPEED = 0.0005
        const val SINKING_MAX_SPEED = 0.005
    }

    init {
        this.setMaxHealth(40)
        this.setHealth(40)
    }
}