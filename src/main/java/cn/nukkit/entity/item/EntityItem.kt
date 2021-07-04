package cn.nukkit.entity.item

import cn.nukkit.Server

/**
 * @author MagicDroidX
 */
class EntityItem(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt) {
    var owner: String? = null
    var thrower: String? = null
    protected var item: Item? = null
    var pickupDelay = 0

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
    val gravity: Float
        get() = 0.04f

    @get:Override
    val drag: Float
        get() = 0.02f

    @get:Override
    protected val baseOffset: Float
        protected get() = 0.125f

    @Override
    fun canCollide(): Boolean {
        return false
    }

    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setMaxHealth(5)
        this.setHealth(this.namedTag.getShort("Health"))
        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age")
        }
        if (this.namedTag.contains("PickupDelay")) {
            pickupDelay = this.namedTag.getShort("PickupDelay")
        }
        if (this.namedTag.contains("Owner")) {
            owner = this.namedTag.getString("Owner")
        }
        if (this.namedTag.contains("Thrower")) {
            thrower = this.namedTag.getString("Thrower")
        }
        if (!this.namedTag.contains("Item")) {
            this.close()
            return
        }
        item = NBTIO.getItemHelper(this.namedTag.getCompound("Item"))
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY, true)
        if (item.isLavaResistant()) {
            this.fireProof = true // Netherite items are fireproof
        }
        this.server.getPluginManager().callEvent(ItemSpawnEvent(this))
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Netherite stuff is immune to fire and lava damage")
    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return if (item != null && item.isLavaResistant() && (source.getCause() === DamageCause.LAVA || source.getCause() === DamageCause.FIRE || source.getCause() === DamageCause.FIRE_TICK)) {
            false
        } else (source.getCause() === DamageCause.VOID || source.getCause() === DamageCause.CONTACT || source.getCause() === DamageCause.FIRE_TICK ||
                (source.getCause() === DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() === DamageCause.BLOCK_EXPLOSION) &&
                !this.isInsideOfWater() && (item == null ||
                item.getId() !== Item.NETHER_STAR)) && super.attack(source)
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
        this.timing.startTiming()
        if (this.age % 60 === 0 && this.onGround && getItem() != null && this.isAlive()) {
            if (getItem().getCount() < getItem().getMaxStackSize()) {
                for (entity in this.getLevel().getNearbyEntities(getBoundingBox().grow(1, 1, 1), this, false)) {
                    if (entity is EntityItem) {
                        if (!entity.isAlive()) {
                            continue
                        }
                        val closeItem: Item? = (entity as EntityItem).getItem()
                        if (!closeItem.equals(getItem(), true, true)) {
                            continue
                        }
                        if (!entity.isOnGround()) {
                            continue
                        }
                        val newAmount: Int = getItem().getCount() + closeItem.getCount()
                        if (newAmount > getItem().getMaxStackSize()) {
                            continue
                        }
                        entity.close()
                        getItem().setCount(newAmount)
                        val packet = EntityEventPacket()
                        packet.eid = getId()
                        packet.data = newAmount
                        packet.event = EntityEventPacket.MERGE_ITEMS
                        Server.broadcastPacket(this.getViewers().values(), packet)
                    }
                }
            }
        }
        var hasUpdate: Boolean = this.entityBaseTick(tickDiff)
        val lavaResistant = fireProof || item != null && item.isLavaResistant()
        if (!lavaResistant && (isInsideOfFire() || isInsideOfLava())) {
            this.kill()
        }
        if (this.isAlive()) {
            if (pickupDelay > 0 && pickupDelay < 32767) {
                pickupDelay -= tickDiff
                if (pickupDelay < 0) {
                    pickupDelay = 0
                }
            } /* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, true)) {
                            return true;
                        }
                    }
                }
            }*/
            var bid: Int = this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 0)
            if (bid == BlockID.WATER || bid == BlockID.STILL_WATER || this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 1).also { bid = it } == BlockID.WATER || bid == BlockID.STILL_WATER) {
                //item is fully in water or in still water
                this.motionY -= gravity * -0.015
            } else if (lavaResistant && (this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 0) === BlockID.LAVA || this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 0) === BlockID.STILL_LAVA || this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 1) === BlockID.LAVA || this.level.getBlockIdAt(this.x as Int, this.boundingBox.getMaxY() as Int, this.z as Int, 1) === BlockID.STILL_LAVA)) {
                //item is fully in lava or in still lava
                this.motionY -= gravity * -0.015
            } else if (this.isInsideOfWater() || lavaResistant && this.isInsideOfLava()) {
                this.motionY = gravity - 0.06 //item is going up in water, don't let it go back down too fast
            } else {
                this.motionY -= gravity //item is not in water
            }
            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true
            }
            this.move(this.motionX, this.motionY, this.motionZ)
            var friction = (1 - drag).toDouble()
            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents(Math.floor(this.x) as Int, Math.floor(this.y - 1) as Int, Math.floor(this.z) as Int - 1)).getFrictionFactor()
            }
            this.motionX *= friction
            this.motionY *= 1 - drag
            this.motionZ *= friction
            if (this.onGround) {
                this.motionY *= -0.5
            }
            this.updateMovement()
            if (this.age > 6000) {
                val ev = ItemDespawnEvent(this)
                this.server.getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    this.age = 0
                } else {
                    this.kill()
                    hasUpdate = true
                }
            }
        }
        this.timing.stopTiming()
        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001
    }

    @Override
    fun setOnFire(seconds: Int) {
        if (item != null && item.isLavaResistant()) {
            return
        }
        super.setOnFire(seconds)
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        if (item != null) { // Yes, a item can be null... I don't know what causes this, but it can happen.
            this.namedTag.putCompound("Item", NBTIO.putItemHelper(item, -1))
            this.namedTag.putShort("Health", this.getHealth() as Int)
            this.namedTag.putShort("Age", this.age)
            this.namedTag.putShort("PickupDelay", pickupDelay)
            if (owner != null) {
                this.namedTag.putString("Owner", owner)
            }
            if (thrower != null) {
                this.namedTag.putString("Thrower", thrower)
            }
        }
    }

    @get:Override
    val name: String
        get() = if (this.hasCustomName()) this.getNameTag() else if (item.hasCustomName()) item.getCustomName() else item.getName()

    fun getItem(): Item? {
        return item
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    @Override
    fun createAddEntityPacket(): DataPacket {
        val addEntity = AddItemEntityPacket()
        addEntity.entityUniqueId = this.getId()
        addEntity.entityRuntimeId = this.getId()
        addEntity.x = this.x as Float
        addEntity.y = this.y as Float
        addEntity.z = this.z as Float
        addEntity.speedX = this.motionX as Float
        addEntity.speedY = this.motionY as Float
        addEntity.speedZ = this.motionZ as Float
        addEntity.metadata = this.dataProperties
        addEntity.item = getItem()
        return addEntity
    }

    @Override
    fun doesTriggerPressurePlate(): Boolean {
        return true
    }

    companion object {
        @get:Override
        val networkId = 64
            get() = Companion.field
        const val DATA_SOURCE_ID = 17
    }
}