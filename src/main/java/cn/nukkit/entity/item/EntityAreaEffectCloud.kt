package cn.nukkit.entity.item

import cn.nukkit.entity.Entity

class EntityAreaEffectCloud(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt) {
    protected var reapplicationDelay = 0
    protected var durationOnUse = 0
    protected var initialRadius = 0f
    protected var radiusOnUse = 0f
    protected var nextApply = 0
    var cloudEffects: List<Effect>? = null
    private var lastAge = 0
    var waitTime: Int
        get() = this.getDataPropertyInt(DATA_AREA_EFFECT_CLOUD_WAITING)
        set(waitTime) {
            setWaitTime(waitTime, true)
        }

    fun setWaitTime(waitTime: Int, send: Boolean) {
        this.setDataProperty(IntEntityData(DATA_AREA_EFFECT_CLOUD_WAITING, waitTime), send)
    }

    var potionId: Int
        get() = this.getDataPropertyShort(DATA_POTION_AUX_VALUE)
        set(potionId) {
            setPotionId(potionId, true)
        }

    fun setPotionId(potionId: Int, send: Boolean) {
        this.setDataProperty(ShortEntityData(DATA_POTION_AUX_VALUE, potionId and 0xFFFF), send)
    }

    @JvmOverloads
    fun recalculatePotionColor(send: Boolean = true) {
        val a: Int
        val r: Int
        val g: Int
        val b: Int
        val color: Int
        if (namedTag.contains("ParticleColor")) {
            color = namedTag.getInt("ParticleColor")
            a = color and -0x1000000 shr 24
            r = color and 0x00FF0000 shr 16
            g = color and 0x0000FF00 shr 8
            b = color and 0x000000FF
        } else {
            a = 255
            val effect: Effect = Potion.getEffect(potionId, true)
            if (effect == null) {
                r = 40
                g = 40
                b = 255
            } else {
                val colors: IntArray = effect.getColor()
                r = colors[0]
                g = colors[1]
                b = colors[2]
            }
        }
        setPotionColor(a, r, g, b, send)
    }

    var potionColor: Int
        get() = this.getDataPropertyInt(DATA_POTION_COLOR)
        set(argp) {
            setPotionColor(argp, true)
        }

    fun setPotionColor(alpha: Int, red: Int, green: Int, blue: Int, send: Boolean) {
        setPotionColor(alpha and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff), send)
    }

    fun setPotionColor(argp: Int, send: Boolean) {
        this.setDataProperty(IntEntityData(DATA_POTION_COLOR, argp), send)
    }

    var pickupCount: Int
        get() = this.getDataPropertyInt(DATA_PICKUP_COUNT)
        set(pickupCount) {
            setPickupCount(pickupCount, true)
        }

    fun setPickupCount(pickupCount: Int, send: Boolean) {
        this.setDataProperty(IntEntityData(DATA_PICKUP_COUNT, pickupCount), send)
    }

    var radiusChangeOnPickup: Float
        get() = this.getDataPropertyFloat(DATA_CHANGE_ON_PICKUP)
        set(radiusChangeOnPickup) {
            setRadiusChangeOnPickup(radiusChangeOnPickup, true)
        }

    fun setRadiusChangeOnPickup(radiusChangeOnPickup: Float, send: Boolean) {
        this.setDataProperty(FloatEntityData(DATA_CHANGE_ON_PICKUP, radiusChangeOnPickup), send)
    }

    var radiusPerTick: Float
        get() = this.getDataPropertyFloat(DATA_CHANGE_RATE)
        set(radiusPerTick) {
            setRadiusPerTick(radiusPerTick, true)
        }

    fun setRadiusPerTick(radiusPerTick: Float, send: Boolean) {
        this.setDataProperty(FloatEntityData(DATA_CHANGE_RATE, radiusPerTick), send)
    }

    var spawnTime: Long
        get() = this.getDataPropertyInt(DATA_SPAWN_TIME).toLong()
        set(spawnTime) {
            setSpawnTime(spawnTime, true)
        }

    fun setSpawnTime(spawnTime: Long, send: Boolean) {
        this.setDataProperty(LongEntityData(DATA_SPAWN_TIME, spawnTime), send)
    }

    var duration: Int
        get() = this.getDataPropertyInt(DATA_DURATION)
        set(duration) {
            setDuration(duration, true)
        }

    fun setDuration(duration: Int, send: Boolean) {
        this.setDataProperty(IntEntityData(DATA_DURATION, duration), send)
    }

    var radius: Float
        get() = this.getDataPropertyFloat(DATA_AREA_EFFECT_CLOUD_RADIUS)
        set(radius) {
            setRadius(radius, true)
        }

    fun setRadius(radius: Float, send: Boolean) {
        this.setDataProperty(FloatEntityData(DATA_AREA_EFFECT_CLOUD_RADIUS, radius), send)
    }

    var particleId: Int
        get() = this.getDataPropertyInt(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID)
        set(particleId) {
            setParticleId(particleId, true)
        }

    fun setParticleId(particleId: Int, send: Boolean) {
        this.setDataProperty(IntEntityData(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, particleId), send)
    }

    @Override
    protected override fun initEntity() {
        super.initEntity()
        this.invulnerable = true
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true)
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IMMOBILE, true)
        this.setDataProperty(ShortEntityData(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, 32), false)
        this.setDataProperty(LongEntityData(DATA_SPAWN_TIME, this.level.getCurrentTick()), false)
        this.setDataProperty(IntEntityData(DATA_PICKUP_COUNT, 0), false)
        cloudEffects = ArrayList(1)
        for (effectTag in namedTag.getList("mobEffects", CompoundTag::class.java).getAll()) {
            val effect: Effect = Effect.getEffect(effectTag.getByte("Id"))
                    .setAmbient(effectTag.getBoolean("Ambient"))
                    .setAmplifier(effectTag.getByte("Amplifier"))
                    .setVisible(effectTag.getBoolean("DisplayOnScreenTextureAnimation"))
                    .setDuration(effectTag.getInt("Duration"))
            cloudEffects.add(effect)
        }
        val displayedPotionId: Int = namedTag.getShort("PotionId")
        setPotionId(displayedPotionId, false)
        recalculatePotionColor()
        if (namedTag.contains("Duration")) {
            setDuration(namedTag.getInt("Duration"), false)
        } else {
            setDuration(600, false)
        }
        durationOnUse = if (namedTag.contains("DurationOnUse")) {
            namedTag.getInt("DurationOnUse")
        } else {
            0
        }
        reapplicationDelay = if (namedTag.contains("ReapplicationDelay")) {
            namedTag.getInt("ReapplicationDelay")
        } else {
            0
        }
        initialRadius = if (namedTag.contains("InitialRadius")) {
            namedTag.getFloat("InitialRadius")
        } else {
            3.0f
        }
        if (namedTag.contains("Radius")) {
            setRadius(namedTag.getFloat("Radius"), false)
        } else {
            setRadius(initialRadius, false)
        }
        if (namedTag.contains("RadiusChangeOnPickup")) {
            setRadiusChangeOnPickup(namedTag.getFloat("RadiusChangeOnPickup"), false)
        } else {
            setRadiusChangeOnPickup(-0.5f, false)
        }
        radiusOnUse = if (namedTag.contains("RadiusOnUse")) {
            namedTag.getFloat("RadiusOnUse")
        } else {
            -0.5f
        }
        if (namedTag.contains("RadiusPerTick")) {
            setRadiusPerTick(namedTag.getFloat("RadiusPerTick"), false)
        } else {
            setRadiusPerTick(-0.005f, false)
        }
        if (namedTag.contains("WaitTime")) {
            setWaitTime(namedTag.getInt("WaitTime"), false)
        } else {
            setWaitTime(10, false)
        }
        setMaxHealth(1)
        setHealth(1)
    }

    @Override
    override fun attack(source: EntityDamageEvent?): Boolean {
        return false
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        val effectsTag: ListTag<CompoundTag> = ListTag("mobEffects")
        for (effect in cloudEffects!!) {
            effectsTag.add(CompoundTag().putByte("Id", effect.getId())
                    .putBoolean("Ambient", effect.isAmbient())
                    .putByte("Amplifier", effect.getAmplifier())
                    .putBoolean("DisplayOnScreenTextureAnimation", effect.isVisible())
                    .putInt("Duration", effect.getDuration())
            )
        }
        //TODO Do we really need to save the entity data to nbt or is it already saved somewhere?
        namedTag.putList(effectsTag)
        namedTag.putInt("ParticleColor", potionColor)
        namedTag.putShort("PotionId", potionId)
        namedTag.putInt("Duration", duration)
        namedTag.putInt("DurationOnUse", durationOnUse)
        namedTag.putInt("ReapplicationDelay", reapplicationDelay)
        namedTag.putFloat("Radius", radius)
        namedTag.putFloat("RadiusChangeOnPickup", radiusChangeOnPickup)
        namedTag.putFloat("RadiusOnUse", radiusOnUse)
        namedTag.putFloat("RadiusPerTick", radiusPerTick)
        namedTag.putInt("WaitTime", waitTime)
        namedTag.putFloat("InitialRadius", initialRadius)
    }

    @Override
    override fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        super.onUpdate(currentTick)
        val sendRadius = age % 10 === 0
        val age: Int = this.age
        var radius = radius
        val waitTime = waitTime
        if (age < waitTime) {
            radius = initialRadius
        } else if (age > waitTime + duration) {
            kill()
        } else {
            val tickDiff = age - lastAge
            radius += radiusPerTick * tickDiff
            if (tickDiff.let { nextApply -= it; nextApply } <= 0) {
                nextApply = reapplicationDelay + 10
                val collidingEntities: Array<Entity> = level.getCollidingEntities(getBoundingBox())
                if (collidingEntities.size > 0) {
                    radius += radiusOnUse
                    radiusOnUse /= 2f
                    duration = duration + durationOnUse
                    for (collidingEntity in collidingEntities) {
                        if (collidingEntity === this || collidingEntity !is EntityLiving) continue
                        for (effect in cloudEffects!!) {
                            if (effect is InstantEffect) {
                                var damage = false
                                if (effect.getId() === Effect.HARMING) damage = true
                                if (collidingEntity.isUndead()) damage = !damage // invert effect if undead
                                if (damage) collidingEntity.attack(EntityDamageByEntityEvent(this, collidingEntity, EntityDamageEvent.DamageCause.MAGIC, (0.5 * (6 shl effect.getAmplifier() + 1).toDouble()).toFloat())) else collidingEntity.heal(EntityRegainHealthEvent(collidingEntity, (0.5 * (4 shl effect.getAmplifier() + 1).toDouble()).toFloat(), EntityRegainHealthEvent.CAUSE_MAGIC))
                                continue
                            }
                            collidingEntity.addEffect(effect)
                        }
                    }
                }
            }
        }
        lastAge = age
        if (radius <= 1.5 && age >= waitTime) {
            setRadius(radius, false)
            kill()
        } else {
            setRadius(radius, sendRadius)
        }
        val height = height
        boundingBox.setBounds(x - radius, y - height, z - radius, x + radius, y + height, z + radius)
        this.setDataProperty(FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, height), false)
        this.setDataProperty(FloatEntityData(DATA_BOUNDING_BOX_WIDTH, radius), false)
        this.timing.stopTiming()
        return true
    }

    @Override
    fun canCollideWith(entity: Entity?): Boolean {
        return entity is EntityLiving
    }

    @get:Override
    override val height: Float
        get() = 0.3f + radius / 2f

    @get:Override
    override val width: Float
        get() = radius

    @get:Override
    override val length: Float
        get() = radius

    @get:Override
    protected override val gravity: Float
        protected get() = 0

    @get:Override
    protected override val drag: Float
        protected get() = 0

    companion object {
        @get:Override
        val networkId = 95
            get() = Companion.field
    }
}