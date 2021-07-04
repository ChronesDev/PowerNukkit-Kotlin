package cn.nukkit.blockentity

import cn.nukkit.block.Block

class BlockEntityConduit(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    private var targetEntity: Entity? = null
    private var target: Long = 0

    // Client validates the structure, so if we set it to an invalid state it would cause a visual desync
    /*public void setActive(boolean active) {
              this.active = active;
          }*/
    var isActive = false
        private set
    var validBlocks = 0
        private set

    @Override
    protected override fun initBlockEntity() {
        validBlocks = -1
        if (!namedTag.contains("Target")) {
            namedTag.putLong("Target", -1)
            target = -1
            targetEntity = null
        } else {
            target = namedTag.getLong("Target")
        }
        isActive = namedTag.getBoolean("Active")
        super.initBlockEntity()
        this.scheduleUpdate()
    }

    @Override
    override fun saveNBT() {
        val targetEntity: Entity? = targetEntity
        namedTag.putLong("Target", if (targetEntity != null) targetEntity.getId() else -1)
        namedTag.putBoolean("Active", isActive)
        super.saveNBT()
    }

    @get:Override
    override var name: String
        get() = "Conduit"
        set(name) {
            super.name = name
        }

    @Override
    override fun onUpdate(): Boolean {
        if (closed) {
            return false
        }
        val activeBeforeUpdate = isActive
        val targetBeforeUpdate: Entity? = targetEntity
        if (validBlocks == -1) {
            isActive = scanStructure()
        }
        if (level.getCurrentTick() % 20 === 0) {
            isActive = scanStructure()
        }
        if (target != -1L) {
            targetEntity = getLevel().getEntity(target)
            target = -1
        }
        if (activeBeforeUpdate != isActive || targetBeforeUpdate !== targetEntity) {
            this.spawnToAll()
            if (activeBeforeUpdate && !isActive) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_DEACTIVATE)
                level.getServer().getPluginManager().callEvent(ConduitDeactivateEvent(getBlock()))
            } else if (!activeBeforeUpdate && isActive) {
                level.addSound(add(0, 0.5, 0), Sound.CONDUIT_ACTIVATE)
                level.getServer().getPluginManager().callEvent(ConduitActivateEvent(getBlock()))
            }
        }
        if (!isActive) {
            targetEntity = null
            target = -1
        } else if (level.getCurrentTick() % 40 === 0) {
            attackMob()
            addEffectToPlayers()
        }
        return true
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getBlock().getId() === BlockID.CONDUIT

    fun setTargetEntity(targetEntity: Entity?) {
        this.targetEntity = targetEntity
    }

    fun getTargetEntity(): Entity? {
        return targetEntity
    }

    fun addEffectToPlayers() {
        val radius = playerRadius
        if (radius <= 0) {
            return
        }
        val radiusSquared = radius * radius
        val conduitPos = Vector2(x, z)
        this.getLevel().getPlayers().values().stream()
                .filter { target: Entity -> canAffect(target) }
                .filter { p -> conduitPos.distanceSquared(p.x, p.z) <= radiusSquared }
                .forEach { p ->
                    p.addEffect(Effect.getEffect(Effect.CONDUIT_POWER)
                            .setDuration(260)
                            .setVisible(true)
                            .setAmplifier(0)
                            .setAmbient(true)
                    )
                }
    }

    fun attackMob() {
        val radius = attackRadius
        if (radius <= 0) {
            return
        }
        var updated = false
        var target: Entity? = targetEntity
        if (target != null && !canAttack(target)) {
            target = null
            updated = true
            targetEntity = null
            this.target = -1
        }
        if (target == null) {
            val mobs: Array<Entity> = Arrays.stream(level.getCollidingEntities(SimpleAxisAlignedBB(x - radius, y - radius, z - radius, x + 1 + radius, y + 1 + radius, z + 1 + radius)))
                    .filter { target: Entity -> canAttack(target) }
                    .toArray { _Dummy_.__Array__() }
            if (mobs.size == 0) {
                if (updated) {
                    spawnToAll()
                }
                return
            }
            target = mobs[ThreadLocalRandom.current().nextInt(mobs.size)]
            targetEntity = target
            updated = true
        }
        if (!target.attack(EntityDamageByBlockEvent(getBlock(), target, EntityDamageEvent.DamageCause.MAGIC, 4))) {
            targetEntity = null
            updated = true
        }
        if (updated) {
            spawnToAll()
        }
    }

    fun canAttack(target: Entity): Boolean {
        return target is EntityMob && canAffect(target)
    }

    fun canAffect(target: Entity): Boolean {
        return (target.isTouchingWater()
                || (target.level.isRaining() && target.level.canBlockSeeSky(target)
                && Biome.getBiome(target.level.getBiomeId(target.getFloorX(), target.getFloorZ())) !is SnowyBiome))
    }

    private fun scanWater(): Boolean {
        val x: Int = getFloorX()
        val y: Int = getFloorY()
        val z: Int = getFloorZ()
        for (ix in -1..1) {
            for (iz in -1..1) {
                for (iy in -1..1) {
                    var blockId: Int = this.getLevel().getBlockIdAt(x + ix, y + iy, z + iz, 0)
                    if (blockId != Block.WATER && blockId != Block.STILL_WATER) {
                        blockId = this.getLevel().getBlockIdAt(x + ix, y + iy, z + iz, 1)
                        if (blockId != Block.WATER && blockId != Block.STILL_WATER) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    private fun scanFrame(): Int {
        var validBlocks = 0
        val x: Int = getFloorX()
        val y: Int = getFloorY()
        val z: Int = getFloorZ()
        for (iy in -2..2) {
            if (iy == 0) {
                for (ix in -2..2) {
                    for (iz in -2..2) {
                        if (Math.abs(iz) !== 2 && Math.abs(ix) !== 2) {
                            continue
                        }
                        val blockId: Int = level.getBlockIdAt(x + ix, y, z + iz)
                        //validBlocks++;
                        //level.setBlock(x + ix, y, z + iz, new BlockPlanks(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++
                        }
                    }
                }
            } else {
                val absIY: Int = Math.abs(iy)
                for (ix in -2..2) {
                    if (absIY != 2 && ix == 0) {
                        continue
                    }
                    if (absIY == 2 || Math.abs(ix) === 2) {
                        val blockId: Int = level.getBlockIdAt(x + ix, y + iy, z)
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++
                        }
                    }
                }
                for (iz in -2..2) {
                    if (absIY != 2 && iz == 0) {
                        continue
                    }
                    if (absIY == 2 && iz != 0 || Math.abs(iz) === 2) {
                        val blockId: Int = level.getBlockIdAt(x, y + iy, z + iz)
                        //validBlocks++;
                        //level.setBlock(x, y + iy, z + iz, new BlockWood(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(blockId)) {
                            validBlocks++
                        }
                    }
                }
            }
        }
        return validBlocks
    }

    fun scanEdgeBlock(): List<Block> {
        val validBlocks: List<Block> = ArrayList()
        val x: Int = getFloorX()
        val y: Int = getFloorY()
        val z: Int = getFloorZ()
        for (iy in -2..2) {
            if (iy != 0) {
                for (ix in -2..2) {
                    for (iz in -2..2) {
                        if (Math.abs(iy) !== 2 && Math.abs(iz) < 2 && Math.abs(ix) < 2) {
                            continue
                        }
                        if (ix == 0 || iz == 0) {
                            continue
                        }
                        val block: Block = level.getBlock(x + ix, y + iy, z + iz)
                        //validBlocks++;
                        //level.setBlock(x + ix, y + iy, z + iz, new BlockDiamond(), true, true);
                        if (VALID_STRUCTURE_BLOCKS.contains(block.getId())) {
                            validBlocks.add(block)
                        }
                    }
                }
            }
        }
        return validBlocks
    }

    fun scanStructure(): Boolean {
        if (!scanWater()) {
            validBlocks = 0
            return false
        }
        val validBlocks = scanFrame()
        if (validBlocks < 16) {
            this.validBlocks = 0
            return false
        }
        this.validBlocks = validBlocks
        return true
    }

    val playerRadius: Int
        get() {
            val radius = validBlocks / 7
            return radius * 16
        }
    val attackRadius: Int
        get() = if (validBlocks >= 42) {
            8
        } else {
            0
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val tag: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.CONDUIT)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putBoolean("Active", isActive)
                    .putBoolean("isMovable", isMovable())
            val targetEntity: Entity? = targetEntity
            tag.putLong("Target", if (targetEntity != null) targetEntity.getId() else -1)
            return tag
        }

    companion object {
        var VALID_STRUCTURE_BLOCKS: IntSet = IntOpenHashSet(intArrayOf(
                BlockID.PRISMARINE,
                BlockID.SEA_LANTERN
        ))
    }
}