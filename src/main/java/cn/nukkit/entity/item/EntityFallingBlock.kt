package cn.nukkit.entity.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX
 */
class EntityFallingBlock(chunk: FullChunk?, nbt: CompoundTag?) : Entity(chunk, nbt) {
    @get:Override
    val width: Float
        get() = 0.98f

    @get:Override
    val length: Float
        get() = 0.98f

    @get:Override
    val height: Float
        get() = 0.98f

    @get:Override
    protected val gravity: Float
        protected get() = 0.04f

    @get:Override
    protected val drag: Float
        protected get() = 0.02f

    @get:Override
    protected val baseOffset: Float
        protected get() = 0.49f

    @Override
    fun canCollide(): Boolean {
        return false
    }

    var block = 0
        protected set
    var damage = 0
        protected set
    protected var breakOnLava = false
    @Override
    protected fun initEntity() {
        super.initEntity()
        if (namedTag != null) {
            if (namedTag.contains("TileID")) {
                block = namedTag.getInt("TileID")
            } else if (namedTag.contains("Tile")) {
                block = namedTag.getInt("Tile")
                namedTag.putInt("TileID", block)
            }
            if (namedTag.contains("Data")) {
                damage = namedTag.getByte("Data")
            }
        }
        breakOnLava = namedTag.getBoolean("BreakOnLava")
        if (block == 0) {
            close()
            return
        }
        this.fireProof = true
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_FIRE_IMMUNE, true)
        setDataProperty(IntEntityData(DATA_VARIANT, GlobalBlockPalette.getOrCreateRuntimeId(block, damage)))
    }

    fun canCollideWith(entity: Entity?): Boolean {
        return false
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return source.getCause() === DamageCause.VOID && super.attack(source)
    }

    @Override
    fun onUpdate(currentTick: Int): Boolean {
        if (closed) {
            return false
        }
        this.timing.startTiming()
        val tickDiff: Int = currentTick - lastUpdate
        if (tickDiff <= 0 && !justCreated) {
            return true
        }
        lastUpdate = currentTick
        var hasUpdate: Boolean = entityBaseTick(tickDiff)
        if (isAlive()) {
            motionY -= gravity
            move(motionX, motionY, motionZ)
            val friction = 1 - drag
            motionX *= friction
            motionY *= 1 - drag
            motionZ *= friction
            val pos: Vector3 = Vector3(x - 0.5, y, z - 0.5).round()
            if (breakOnLava && level.getBlock(pos.subtract(0, 1, 0)) is BlockLava) {
                close()
                if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    getLevel().dropItem(this, Block.get(block, damage).toItem())
                }
                level.addParticle(DestroyBlockParticle(pos, Block.get(block, damage)))
                return true
            }
            if (onGround) {
                close()
                val block: Block = level.getBlock(pos)
                val floorPos: Vector3 = Vector3(x - 0.5, y, z - 0.5).floor()
                val floorBlock: Block = this.level.getBlock(floorPos)
                if (this.block == Block.SNOW_LAYER && floorBlock.getId() === Block.SNOW_LAYER && floorBlock.getDamage() and 0x7 !== 0x7) {
                    val mergedHeight: Int = (floorBlock.getDamage() and 0x7) + 1 + (damage and 0x7) + 1
                    if (mergedHeight > 8) {
                        val event = EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, 0x7))
                        this.server.getPluginManager().callEvent(event)
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true)
                            val abovePos: Vector3 = floorPos.up()
                            val aboveBlock: Block = this.level.getBlock(abovePos)
                            if (aboveBlock.getId() === Block.AIR) {
                                val event2 = EntityBlockChangeEvent(this, aboveBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 8 - 1))
                                this.server.getPluginManager().callEvent(event2)
                                if (!event2.isCancelled()) {
                                    this.level.setBlock(abovePos, event2.getTo(), true)
                                }
                            }
                        }
                    } else {
                        val event = EntityBlockChangeEvent(this, floorBlock, Block.get(Block.SNOW_LAYER, mergedHeight - 1))
                        this.server.getPluginManager().callEvent(event)
                        if (!event.isCancelled()) {
                            this.level.setBlock(floorPos, event.getTo(), true)
                        }
                    }
                } else if (block.getId() > 0 && block.isTransparent() && !block.canBeReplaced() || this.block == Block.SNOW_LAYER && block is BlockLiquid) {
                    if (if (this.block != Block.SNOW_LAYER) this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS) else this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                        getLevel().dropItem(this, Block.get(this.block, damage).toItem())
                    }
                } else {
                    val event = EntityBlockChangeEvent(this, block, Block.get(block, damage))
                    server.getPluginManager().callEvent(event)
                    if (!event.isCancelled()) {
                        getLevel().setBlock(pos, event.getTo(), true)
                        if (event.getTo().getId() === Item.ANVIL) {
                            getLevel().addLevelEvent(block, LevelEventPacket.EVENT_SOUND_ANVIL_FALL)
                        }
                    }
                }
                hasUpdate = true
            }
            updateMovement()
        }
        this.timing.stopTiming()
        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001
    }

    @Override
    fun saveNBT() {
        namedTag.putInt("TileID", block)
        namedTag.putByte("Data", damage)
    }

    @Override
    fun canBeMovedByCurrents(): Boolean {
        return false
    }

    companion object {
        @get:Override
        val networkId = 66
            get() = Companion.field
    }
}