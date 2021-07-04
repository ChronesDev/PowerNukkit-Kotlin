package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/26
 */
class EntityXPOrb(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt) {
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
        protected get() = 0.04f

    @get:Override
    protected val drag: Float
        protected get() = 0.02f

    @Override
    fun canCollide(): Boolean {
        return false
    }

    private var age = 0
    var pickupDelay = 0
    private var exp = 0
    var closestPlayer: Player? = null
    @Override
    protected fun initEntity() {
        super.initEntity()
        setMaxHealth(5)
        setHealth(5)
        if (namedTag.contains("Health")) {
            this.setHealth(namedTag.getShort("Health"))
        }
        if (namedTag.contains("Age")) {
            age = namedTag.getShort("Age")
        }
        if (namedTag.contains("PickupDelay")) {
            pickupDelay = namedTag.getShort("PickupDelay")
        }
        if (namedTag.contains("Value")) {
            exp = namedTag.getShort("Value")
        }
        if (exp <= 0) {
            exp = 1
        }
        this.dataProperties.putInt(DATA_EXPERIENCE_VALUE, exp)

        //call event item spawn event
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return (source.getCause() === DamageCause.VOID || source.getCause() === DamageCause.FIRE_TICK || (source.getCause() === DamageCause.ENTITY_EXPLOSION ||
                source.getCause() === DamageCause.BLOCK_EXPLOSION) &&
                !this.isInsideOfWater()) && super.attack(source)
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
        var hasUpdate: Boolean = entityBaseTick(tickDiff)
        if (this.isAlive()) {
            if (pickupDelay > 0 && pickupDelay < 32767) { //Infinite delay
                pickupDelay -= tickDiff
                if (pickupDelay < 0) {
                    pickupDelay = 0
                }
            } /* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getCollidingEntities(this.boundingBox, this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, false)) {
                            return true;
                        }
                    }
                }
            }*/
            this.motionY -= gravity
            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true
            }
            if (closestPlayer == null || closestPlayer.distanceSquared(this) > 64.0) {
                closestPlayer = null
                var closestDistance = 0.0
                for (p in this.getViewers().values()) {
                    if (!p.isSpectator() && p.spawned && p.isAlive()) {
                        val d: Double = p.distanceSquared(this)
                        if (d <= 64.0 && (closestPlayer == null || d < closestDistance)) {
                            closestPlayer = p
                            closestDistance = d
                        }
                    }
                }
            }
            if (closestPlayer != null && (closestPlayer.isSpectator() || !closestPlayer.spawned || !closestPlayer.isAlive())) {
                closestPlayer = null
            }
            if (closestPlayer != null) {
                val dX: Double = (closestPlayer.x - this.x) / 8.0
                val dY: Double = (closestPlayer.y + closestPlayer.getEyeHeight() as Double / 2.0 - this.y) / 8.0
                val dZ: Double = (closestPlayer.z - this.z) / 8.0
                val d: Double = Math.sqrt(dX * dX + dY * dY + dZ * dZ)
                var diff = 1.0 - d
                if (diff > 0.0) {
                    diff = diff * diff
                    this.motionX += dX / d * diff * 0.1
                    this.motionY += dY / d * diff * 0.1
                    this.motionZ += dZ / d * diff * 0.1
                }
            }
            this.move(this.motionX, this.motionY, this.motionZ)
            var friction = 1.0 - drag
            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction = this.getLevel().getBlock(this.temporalVector.setComponents(Math.floor(this.x) as Int, Math.floor(this.y - 1) as Int, Math.floor(this.z) as Int - 1)).getFrictionFactor() * friction
            }
            this.motionX *= friction
            this.motionY *= 1 - drag
            this.motionZ *= friction
            if (this.onGround) {
                this.motionY *= -0.5
            }
            this.updateMovement()
            if (age > 6000) {
                this.kill()
                hasUpdate = true
            }
        }
        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        this.namedTag.putShort("Health", getHealth() as Int)
        this.namedTag.putShort("Age", age)
        this.namedTag.putShort("PickupDelay", pickupDelay)
        this.namedTag.putShort("Value", exp)
    }

    fun getExp(): Int {
        return exp
    }

    fun setExp(exp: Int) {
        if (exp <= 0) {
            throw IllegalArgumentException("XP amount must be greater than 0, got $exp")
        }
        this.exp = exp
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    companion object {
        @get:Override
        val networkId = 69
            get() = Companion.field

        /**
         * Split sizes used for dropping experience orbs.
         */
        val ORB_SPLIT_SIZES = intArrayOf(2477, 1237, 617, 307, 149, 73, 37, 17, 7, 3, 1) //This is indexed biggest to smallest so that we can return as soon as we found the biggest value.

        /**
         * Returns the largest size of normal XP orb that will be spawned for the specified amount of XP. Used to split XP
         * up into multiple orbs when an amount of XP is dropped.
         */
        fun getMaxOrbSize(amount: Int): Int {
            for (split in ORB_SPLIT_SIZES) {
                if (amount >= split) {
                    return split
                }
            }
            return 1
        }

        /**
         * Splits the specified amount of XP into an array of acceptable XP orb sizes.
         */
        fun splitIntoOrbSizes(amount: Int): List<Integer> {
            var amount = amount
            val result: List<Integer> = IntArrayList()
            while (amount > 0) {
                val size = getMaxOrbSize(amount)
                result.add(size)
                amount -= size
            }
            return result
        }
    }
}