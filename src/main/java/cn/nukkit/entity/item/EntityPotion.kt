package cn.nukkit.entity.item

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 */
class EntityPotion : EntityProjectile {
    var potionId = 0

    constructor(chunk: FullChunk?, nbt: CompoundTag?) : super(chunk, nbt) {}
    constructor(chunk: FullChunk?, nbt: CompoundTag?, shootingEntity: Entity?) : super(chunk, nbt, shootingEntity) {}

    @Override
    protected fun initEntity() {
        super.initEntity()
        potionId = this.namedTag.getShort("PotionId")
        this.dataProperties.putShort(DATA_POTION_AUX_VALUE, potionId)

        /*Effect effect = Potion.getEffect(potionId, true); TODO: potion color

        if(effect != null) {
            int count = 0;
            int[] c = effect.getColor();
            count += effect.getAmplifier() + 1;

            int r = ((c[0] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int g = ((c[1] * (effect.getAmplifier() + 1)) / count) & 0xff;
            int b = ((c[2] * (effect.getAmplifier() + 1)) / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_UNKNOWN, (r << 16) + (g << 8) + b));
        }*/
    }

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
        protected get() = 0.05f

    @get:Override
    protected val drag: Float
        protected get() = 0.01f

    @Override
    fun onCollideWithEntity(entity: Entity?) {
        splash(entity)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    protected fun splash(collidedWith: Entity?) {
        var potion: Potion = Potion.getPotion(potionId)
        val event = PotionCollideEvent(potion, this)
        this.server.getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return
        }
        this.close()
        potion = event.getPotion()
        if (potion == null) {
            return
        }
        potion.setSplash(true)
        val particle: Particle
        val r: Int
        val g: Int
        val b: Int
        val effect: Effect = Potion.getEffect(potion.getId(), true)
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
        particle = SpellParticle(this, r, g, b)
        this.getLevel().addParticle(particle)
        this.getLevel().addSound(this, Sound.RANDOM_GLASS)
        val entities: Array<Entity> = this.getLevel().getNearbyEntities(this.getBoundingBox().grow(4.125, 2.125, 4.125))
        for (anEntity in entities) {
            val distance: Double = anEntity.distanceSquared(this)
            if (distance < 16) {
                val d: Double = if (anEntity.equals(collidedWith)) 1 else 1 - Math.sqrt(distance) / 4
                potion.applyPotion(anEntity, d)
            }
        }
    }

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (this.closed) {
            return false
        }
        this.timing.startTiming()
        var hasUpdate: Boolean = super.onUpdate(currentTick)
        if (this.age > 1200) {
            this.kill()
            hasUpdate = true
        } else if (this.isCollided) {
            splash(null)
            hasUpdate = true
        }
        this.timing.stopTiming()
        return hasUpdate
    }

    companion object {
        @get:Override
        val networkId = 86
            get() = Companion.field
        const val DATA_POTION_ID = 37
    }
}