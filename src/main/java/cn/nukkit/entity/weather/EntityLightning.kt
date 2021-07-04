package cn.nukkit.entity.weather

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author boybook
 * @since 2016/2/27
 */
class EntityLightning(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt), EntityLightningStrike {
    override var isEffect = true
    var state = 0
    var liveTime = 0
    @Override
    protected fun initEntity() {
        super.initEntity()
        this.setHealth(4)
        this.setMaxHealth(4)
        state = 2
        liveTime = ThreadLocalRandom.current().nextInt(3) + 1
        if (isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && this.server.getDifficulty() >= 2) {
            val block: Block = this.getLevelBlock()
            if (block.getId() === 0 || block.getId() === Block.TALL_GRASS) {
                val fire: BlockFire = Block.get(BlockID.FIRE) as BlockFire
                fire.x = block.x
                fire.y = block.y
                fire.z = block.z
                fire.level = level
                this.getLevel().setBlock(fire, fire, true)
                if (fire.isBlockTopFacingSurfaceSolid(fire.down()) || fire.canNeighborBurn()) {
                    val e = BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING)
                    getServer().getPluginManager().callEvent(e)
                    if (!e.isCancelled()) {
                        level.setBlock(fire, fire, true)
                        level.scheduleUpdate(fire, fire.tickRate() + ThreadLocalRandom.current().nextInt(10))
                    }
                }
            }
        }
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        //false?
        source.setDamage(0)
        return super.attack(source)
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
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
        this.entityBaseTick(tickDiff)
        if (state == 2) {
            this.level.addSound(this, Sound.AMBIENT_WEATHER_THUNDER)
            this.level.addSound(this, Sound.RANDOM_EXPLODE)
        }
        state--
        if (state < 0) {
            if (liveTime == 0) {
                this.close()
                return false
            } else if (state < -ThreadLocalRandom.current().nextInt(10)) {
                liveTime--
                state = 1
                if (isEffect && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
                    val block: Block = this.getLevelBlock()
                    if (block.getId() === Block.AIR || block.getId() === Block.TALL_GRASS) {
                        val e = BlockIgniteEvent(block, null, this, BlockIgniteEvent.BlockIgniteCause.LIGHTNING)
                        getServer().getPluginManager().callEvent(e)
                        if (!e.isCancelled()) {
                            val fire: Block = Block.get(BlockID.FIRE)
                            this.level.setBlock(block, fire)
                            this.getLevel().scheduleUpdate(fire, fire.tickRate())
                        }
                    }
                }
            }
        }
        if (state >= 0) {
            if (isEffect) {
                val bb: AxisAlignedBB = getBoundingBox().grow(3, 3, 3)
                bb.setMaxX(bb.getMaxX() + 6)
                for (entity in this.level.getCollidingEntities(bb, this)) {
                    entity.onStruckByLightning(this)
                }
            }
        }
        return true
    }

    companion object {
        @get:Override
        val networkId = 93
            get() = Companion.field
    }
}