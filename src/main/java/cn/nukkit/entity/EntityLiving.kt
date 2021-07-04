package cn.nukkit.entity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EntityLiving(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt), EntityDamageable {
    @get:Override
    protected override val gravity: Float
        protected get() = 0.08f

    @get:Override
    protected override val drag: Float
        protected get() = 0.02f
    protected var attackTime = 0
    private var attackTimeByShieldKb = false
    private var attackTimeBefore = 0
    protected var invisible = false
    var movementSpeed = 0.1f
    protected var turtleTicks = 0
    @Override
    protected override fun initEntity() {
        super.initEntity()
        if (this.namedTag.contains("HealF")) {
            this.namedTag.putFloat("Health", this.namedTag.getShort("HealF"))
            this.namedTag.remove("HealF")
        }
        if (!this.namedTag.contains("Health") || this.namedTag.get("Health") !is FloatTag) {
            this.namedTag.putFloat("Health", this.getMaxHealth())
        }
        this.health = this.namedTag.getFloat("Health")
    }

    @Override
    override fun setHealth(health: Float) {
        val wasAlive: Boolean = this.isAlive()
        super.setHealth(health)
        if (this.isAlive() && !wasAlive) {
            val pk = EntityEventPacket()
            pk.eid = this.getId()
            pk.event = EntityEventPacket.RESPAWN
            Server.broadcastPacket(this.hasSpawned.values(), pk)
        }
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putFloat("Health", this.getHealth())
    }

    fun hasLineOfSight(entity: Entity?): Boolean {
        //todo
        return true
    }

    fun collidingWith(ent: Entity) { // can override (IronGolem|Bats)
        ent.applyEntityCollision(this)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun attack(source: EntityDamageEvent): Boolean {
        if (this.noDamageTicks > 0) {
            return false
        } else if (attackTime > 0 && !attackTimeByShieldKb) {
            val lastCause: EntityDamageEvent = this.getLastDamageCause()
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false
            }
        }
        if (isBlocking && blockedByShield(source)) {
            return false
        }
        return if (super.attack(source)) {
            if (source is EntityDamageByEntityEvent) {
                var damager: Entity = (source as EntityDamageByEntityEvent).getDamager()
                if (source is EntityDamageByChildEntityEvent) {
                    damager = (source as EntityDamageByChildEntityEvent).getChild()
                }

                //Critical hit
                if (damager is Player && !damager.onGround) {
                    val animate = AnimatePacket()
                    animate.action = AnimatePacket.Action.CRITICAL_HIT
                    animate.eid = getId()
                    this.getLevel().addChunkPacket(damager.getChunkX(), damager.getChunkZ(), animate)
                    this.getLevel().addSound(this, Sound.GAME_PLAYER_ATTACK_STRONG)
                    source.setDamage(source.getDamage() * 1.5f)
                }
                if (damager.isOnFire() && damager !is Player) {
                    this.setOnFire(2 * this.server.getDifficulty())
                }
                val deltaX: Double = this.x - damager.x
                val deltaZ: Double = this.z - damager.z
                this.knockBack(damager, source.getDamage(), deltaX, deltaZ, (source as EntityDamageByEntityEvent).getKnockBack())
            }
            val pk = EntityEventPacket()
            pk.eid = this.getId()
            pk.event = if (this.getHealth() <= 0) EntityEventPacket.DEATH_ANIMATION else EntityEventPacket.HURT_ANIMATION
            Server.broadcastPacket(this.hasSpawned.values(), pk)
            attackTime = source.getAttackCooldown()
            attackTimeByShieldKb = false
            this.scheduleUpdate()
            true
        } else {
            false
        }
    }

    fun knockBack(attacker: Entity?, damage: Double, x: Double, z: Double) {
        this.knockBack(attacker, damage, x, z, 0.4)
    }

    fun knockBack(attacker: Entity?, damage: Double, x: Double, z: Double, base: Double) {
        var f: Double = Math.sqrt(x * x + z * z)
        if (f <= 0) {
            return
        }
        f = 1 / f
        val motion = Vector3(this.motionX, this.motionY, this.motionZ)
        motion.x /= 2.0
        motion.y /= 2.0
        motion.z /= 2.0
        motion.x += x * f * base
        motion.y += base
        motion.z += z * f * base
        if (motion.y > base) {
            motion.y = base
        }
        this.setMotion(motion)
    }

    @Override
    override fun kill() {
        if (!this.isAlive()) {
            return
        }
        super.kill()
        val ev = EntityDeathEvent(this, drops)
        this.server.getPluginManager().callEvent(ev)
        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (item in ev.getDrops()) {
                this.getLevel().dropItem(this, item)
            }
        }
    }

    @Override
    fun entityBaseTick(): Boolean {
        return this.entityBaseTick(1)
    }

    @Override
    override fun entityBaseTick(tickDiff: Int): Boolean {
        Timings.livingEntityBaseTickTimer.startTiming()
        var isBreathing: Boolean = !this.isInsideOfWater()
        if (this is Player) {
            if (isBreathing && (this as Player).getInventory().getHelmet() is ItemTurtleShell) {
                turtleTicks = 200
            } else if (turtleTicks > 0) {
                isBreathing = true
                turtleTicks--
            }
            if ((this as Player).isCreative() || (this as Player).isSpectator()) {
                isBreathing = true
            }
        }
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_BREATHING, isBreathing)
        var hasUpdate: Boolean = super.entityBaseTick(tickDiff)
        if (this.isAlive()) {
            if (this.isInsideOfSolid()) {
                hasUpdate = true
                attack(EntityDamageEvent(this, DamageCause.SUFFOCATION, 1))
            }
            if (this.isOnLadder() || this.hasEffect(Effect.LEVITATION) || this.hasEffect(Effect.SLOW_FALLING)) {
                this.resetFallDistance()
            }
            if (!this.hasEffect(Effect.WATER_BREATHING) && !this.hasEffect(Effect.CONDUIT_POWER) && this.isInsideOfWater()) {
                if (this is EntityWaterAnimal || this is Player && ((this as Player).isCreative() || (this as Player).isSpectator())) {
                    airTicks = 400
                } else {
                    if (turtleTicks == 0 || turtleTicks == 200) {
                        hasUpdate = true
                        var airTicks = airTicks - tickDiff
                        if (airTicks <= -20) {
                            airTicks = 0
                            attack(EntityDamageEvent(this, DamageCause.DROWNING, 2))
                        }
                        airTicks = airTicks
                    }
                }
            } else {
                if (this is EntityWaterAnimal) {
                    hasUpdate = true
                    var airTicks = airTicks - tickDiff
                    if (airTicks <= -20) {
                        airTicks = 0
                        attack(EntityDamageEvent(this, DamageCause.SUFFOCATION, 2))
                    }
                    airTicks = airTicks
                } else {
                    var airTicks = airTicks
                    if (airTicks < 400) {
                        airTicks = Math.min(400, airTicks + tickDiff * 5)
                    }
                }
            }
        }
        if (attackTime > 0) {
            attackTime -= tickDiff
            if (attackTime <= 0) {
                attackTimeByShieldKb = false
            }
            hasUpdate = true
        }
        if (this.riding == null) {
            for (entity in level.getNearbyEntities(this.boundingBox.grow(0.20000000298023224, 0.0, 0.20000000298023224), this)) {
                if (entity is EntityRideable) {
                    collidingWith(entity)
                }
            }
        }

        // Used to check collisions with magma blocks
        val block: Block = this.level.getBlock(x as Int, y as Int - 1, z as Int)
        if (block is BlockMagma) block.onEntityCollide(this)
        Timings.livingEntityBaseTickTimer.stopTiming()
        return hasUpdate
    }

    val drops: Array<Any>
        get() = Item.EMPTY_ARRAY

    fun getLineOfSight(maxDistance: Int): Array<Block> {
        return this.getLineOfSight(maxDistance, 0)
    }

    fun getLineOfSight(maxDistance: Int, maxLength: Int): Array<Block> {
        return this.getLineOfSight(maxDistance, maxLength, arrayOf<Integer>())
    }

    @Deprecated
    fun getLineOfSight(maxDistance: Int, maxLength: Int, transparent: Map<Integer?, Object?>): Array<Block> {
        return this.getLineOfSight(maxDistance, maxLength, transparent.keySet().toArray(Utils.EMPTY_INTEGERS))
    }

    fun getLineOfSight(maxDistance: Int, maxLength: Int, transparent: Array<Integer?>?): Array<Block> {
        var maxDistance = maxDistance
        var transparent: Array<Integer?>? = transparent
        if (maxDistance > 120) {
            maxDistance = 120
        }
        if (transparent != null && transparent.size == 0) {
            transparent = null
        }
        val blocks: List<Block> = ArrayList()
        val itr = BlockIterator(this.level, this.getPosition(), this.getDirectionVector(), this.getEyeHeight(), maxDistance)
        while (itr.hasNext()) {
            val block: Block = itr.next()
            blocks.add(block)
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0)
            }
            val id: Int = block.getId()
            if (transparent == null) {
                if (id != 0) {
                    break
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break
                }
            }
        }
        return blocks.toArray(Block.EMPTY_ARRAY)
    }

    fun getTargetBlock(maxDistance: Int): Block {
        return getTargetBlock(maxDistance, arrayOf<Integer>())
    }

    @Deprecated
    fun getTargetBlock(maxDistance: Int, transparent: Map<Integer?, Object?>): Block {
        return getTargetBlock(maxDistance, transparent.keySet().toArray(Utils.EMPTY_INTEGERS))
    }

    fun getTargetBlock(maxDistance: Int, transparent: Array<Integer?>?): Block? {
        try {
            val blocks: Array<Block> = this.getLineOfSight(maxDistance, 1, transparent)
            val block: Block = blocks[0]
            if (block != null) {
                if (transparent != null && transparent.size != 0) {
                    if (Arrays.binarySearch(transparent, block.getId()) < 0) {
                        return block
                    }
                } else {
                    return block
                }
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    var airTicks: Int
        get() = this.getDataPropertyShort(DATA_AIR)
        set(ticks) {
            this.setDataProperty(ShortEntityData(DATA_AIR, ticks))
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun blockedByShield(source: EntityDamageEvent): Boolean {
        var damager: Entity? = null
        if (source is EntityDamageByChildEntityEvent) {
            damager = (source as EntityDamageByChildEntityEvent).getChild()
        } else if (source is EntityDamageByEntityEvent) {
            damager = (source as EntityDamageByEntityEvent).getDamager()
        }
        if (damager == null || damager is EntityWeather || !isBlocking) {
            return false
        }
        val entityPos: Vector3 = damager.getPosition()
        val direction: Vector3 = this.getDirectionVector()
        val normalizedVector: Vector3 = this.getPosition().subtract(entityPos).normalize()
        val blocked: Boolean = normalizedVector.x * direction.x + normalizedVector.z * direction.z < 0.0
        val knockBack = damager !is EntityProjectile
        val event = EntityDamageBlockedEvent(this, source, knockBack, true)
        if (!blocked || !source.canBeReducedByArmor()) {
            event.setCancelled()
        }
        getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        if (event.getKnockBackAttacker() && damager is EntityLiving) {
            val attacker = damager
            val deltaX: Double = attacker.getX() - this.getX()
            val deltaZ: Double = attacker.getZ() - this.getZ()
            attacker.knockBack(this, 0.0, deltaX, deltaZ)
            attacker.attackTime = 10
            attacker.attackTimeByShieldKb = true
        }
        onBlock(damager, event.getAnimation())
        return true
    }

    protected fun onBlock(entity: Entity?, animate: Boolean) {
        if (animate) {
            getLevel().addSound(this, Sound.ITEM_SHIELD_BLOCK)
        }
    }

    var isBlocking: Boolean
        get() = this.getDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_BLOCKING)
        set(value) {
            this.setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_BLOCKING, value)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isPersistent: Boolean
        get() = namedTag.containsByte("Persistent") && namedTag.getBoolean("Persistent")
        set(persistent) {
            namedTag.putBoolean("Persistent", persistent)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun preAttack(player: Player?) {
        if (attackTimeByShieldKb) {
            attackTimeBefore = attackTime
            attackTime = 0
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun postAttack(player: Player?) {
        if (attackTimeByShieldKb && attackTime == 0) {
            attackTime = attackTimeBefore
        }
    }
}