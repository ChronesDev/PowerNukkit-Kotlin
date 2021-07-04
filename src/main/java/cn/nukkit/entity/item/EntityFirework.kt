package cn.nukkit.entity.item

import cn.nukkit.Server

/**
 * @author CreeperFace
 */
class EntityFirework @PowerNukkitDifference(info = "Will default to a black-creeper-face if the firework data is missing", since = "1.3.1.2-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : Entity(chunk, nbt) {
    private var fireworkAge = 0
    private val lifetime: Int
    private var firework: Item? = null
    private var hadCollision = false
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
        var hasUpdate: Boolean = this.entityBaseTick(tickDiff)
        if (this.isAlive()) {
            this.motionX *= 1.15
            this.motionZ *= 1.15
            this.motionY += 0.04
            val position: Position = getPosition()
            val motion: Vector3 = getMotion()
            this.move(this.motionX, this.motionY, this.motionZ)
            if (this.isCollided && !hadCollision) { //collide with block
                hadCollision = true
                for (collisionBlock in level.getCollisionBlocks(getBoundingBox().grow(0.1, 0.1, 0.1))) {
                    collisionBlock.onProjectileHit(this, position, motion)
                }
            } else if (!this.isCollided && hadCollision) {
                hadCollision = false
            }
            this.updateMovement()
            val f = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) as Float
            this.yaw = (Math.atan2(this.motionX, this.motionZ) * (180.0 / Math.PI)) as Float
            this.pitch = (Math.atan2(this.motionY, f) * (180.0 / Math.PI)) as Float
            if (fireworkAge == 0) {
                this.getLevel().addSound(this, Sound.FIREWORK_LAUNCH)
            }
            fireworkAge++
            hasUpdate = true
            if (fireworkAge >= lifetime) {
                val pk = EntityEventPacket()
                pk.data = 0
                pk.event = EntityEventPacket.FIREWORK_EXPLOSION
                pk.eid = this.getId()
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, networkId)
                Server.broadcastPacket(getViewers().values(), pk)
                this.kill()
                hasUpdate = true
            }
        }
        this.timing.stopTiming()
        return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return ((source.getCause() === DamageCause.VOID || source.getCause() === DamageCause.FIRE_TICK || source.getCause() === DamageCause.ENTITY_EXPLOSION || source.getCause() === DamageCause.BLOCK_EXPLOSION)
                && super.attack(source))
    }

    fun setFirework(item: Item) {
        firework = item
        this.setDataProperty(NBTEntityData(Entity.DATA_DISPLAY_ITEM, item.getNamedTag()))
    }

    @get:Override
    val width: Float
        get() = 0.25f

    @get:Override
    val height: Float
        get() = 0.25f

    companion object {
        @get:Override
        val networkId = 72
            get() = Companion.field
    }

    init {
        val rand = Random()
        lifetime = 30 + rand.nextInt(6) + rand.nextInt(7)
        this.motionX = rand.nextGaussian() * 0.001
        this.motionZ = rand.nextGaussian() * 0.001
        this.motionY = 0.05
        if (nbt.contains("FireworkItem")) {
            firework = NBTIO.getItemHelper(nbt.getCompound("FireworkItem"))
        } else {
            firework = ItemFirework()
        }
        if (!firework.hasCompoundTag() || !firework.getNamedTag().contains("Fireworks")) {
            var tag: CompoundTag? = firework.getNamedTag()
            if (tag == null) {
                tag = CompoundTag()
            }
            val ex: CompoundTag = CompoundTag()
                    .putByteArray("FireworkColor", byteArrayOf(DyeColor.BLACK.getDyeData() as Byte))
                    .putByteArray("FireworkFade", byteArrayOf())
                    .putBoolean("FireworkFlicker", false)
                    .putBoolean("FireworkTrail", false)
                    .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.CREEPER_SHAPED.ordinal())
            tag.putCompound("Fireworks", CompoundTag("Fireworks")
                    .putList(ListTag<CompoundTag>("Explosions").add(ex))
                    .putByte("Flight", 1)
            )
            firework.setNamedTag(tag)
        }
        this.setDataProperty(NBTEntityData(Entity.DATA_DISPLAY_ITEM, firework.getNamedTag()))
        this.setDataProperty(IntEntityData(Entity.DATA_DISPLAY_OFFSET, 1))
        this.setDataProperty(ByteEntityData(Entity.DATA_HAS_DISPLAY, 1))
    }
}