package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockLava @JvmOverloads constructor(meta: Int = 0) : BlockLiquid(meta) {
    @get:Override
    override val id: Int
        get() = LAVA

    @get:Override
    override val lightLevel: Int
        get() = 15

    @get:Override
    override val name: String
        get() = "Lava"

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.highestPosition -= (entity.highestPosition - entity.y) * 0.5

        // Always setting the duration to 15 seconds? TODO
        val ev = EntityCombustByBlockEvent(this, entity, 15)
        Server.getInstance().getPluginManager().callEvent(ev)
        if (!ev.isCancelled() // Making sure the entity is actually alive and not invulnerable.
                && entity.isAlive()
                && entity.noDamageTicks === 0) {
            entity.setOnFire(ev.getDuration())
        }
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(EntityDamageByBlockEvent(this, entity, DamageCause.LAVA, 4))
        }
        super.onEntityCollide(entity)
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val ret: Boolean = this.getLevel().setBlock(this, this, true, false)
        this.getLevel().scheduleUpdate(this, tickRate())
        return ret
    }

    @Override
    override fun onUpdate(type: Int): Int {
        val result: Int = super.onUpdate(type)
        if (type == Level.BLOCK_UPDATE_RANDOM && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            val random: Random = ThreadLocalRandom.current()
            val i: Int = random.nextInt(3)
            if (i > 0) {
                for (k in 0 until i) {
                    val v: Vector3 = this.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1)
                    val block: Block = this.getLevel().getBlock(v)
                    if (block.getId() === AIR) {
                        if (isSurroundingBlockFlammable(block)) {
                            val e = BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA)
                            this.level.getServer().getPluginManager().callEvent(e)
                            if (!e.isCancelled()) {
                                val fire: Block = Block.get(BlockID.FIRE)
                                this.getLevel().setBlock(v, fire, true)
                                this.getLevel().scheduleUpdate(fire, fire.tickRate())
                                return Level.BLOCK_UPDATE_RANDOM
                            }
                            return 0
                        }
                    } else if (block.isSolid()) {
                        return Level.BLOCK_UPDATE_RANDOM
                    }
                }
            } else {
                for (k in 0..2) {
                    val v: Vector3 = this.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1)
                    val block: Block = this.getLevel().getBlock(v)
                    if (block.up().getId() === AIR && block.getBurnChance() > 0) {
                        val e = BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.LAVA)
                        this.level.getServer().getPluginManager().callEvent(e)
                        if (!e.isCancelled()) {
                            val fire: Block = Block.get(BlockID.FIRE)
                            this.getLevel().setBlock(v, fire, true)
                            this.getLevel().scheduleUpdate(fire, fire.tickRate())
                        }
                    }
                }
            }
        }
        return result
    }

    protected fun isSurroundingBlockFlammable(block: Block): Boolean {
        for (face in BlockFace.values()) {
            if (block.getSide(face).getBurnChance() > 0) {
                return true
            }
        }
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.LAVA_BLOCK_COLOR

    @Override
    override fun getBlock(meta: Int): BlockLiquid {
        return Block.get(BlockID.LAVA, meta) as BlockLiquid
    }

    @Override
    override fun tickRate(): Int {
        return if (this.level.getDimension() === Level.DIMENSION_NETHER) {
            10
        } else 30
    }

    @get:Override
    override val flowDecayPerBlock: Int
        get() = if (this.level.getDimension() === Level.DIMENSION_NETHER) {
            1
        } else 2

    @Override
    protected override fun checkForHarden() {
        var colliding: Block? = null
        val down: Block = this.getSide(BlockFace.DOWN)
        for (side in 1..5) { //don't check downwards side
            val blockSide: Block = this.getSide(BlockFace.fromIndex(side))
            if (blockSide is BlockWater || blockSide.getLevelBlockAtLayer(1) is BlockWater) {
                colliding = blockSide
                break
            }
            if (down is BlockSoulSoil) {
                if (blockSide is BlockBlueIce) {
                    liquidCollide(this, Block.get(BlockID.BASALT))
                    return
                }
            }
        }
        if (colliding != null) {
            if (this.getDamage() === 0) {
                this.liquidCollide(colliding, Block.get(BlockID.OBSIDIAN))
            } else if (this.getDamage() <= 4) {
                this.liquidCollide(colliding, Block.get(BlockID.COBBLESTONE))
            }
        }
    }

    @Override
    protected override fun flowIntoBlock(block: Block, newFlowDecay: Int) {
        if (block is BlockWater) {
            (block as BlockLiquid).liquidCollide(this, Block.get(BlockID.STONE))
        } else {
            super.flowIntoBlock(block, newFlowDecay)
        }
    }

    @Override
    override fun addVelocityToEntity(entity: Entity, vector: Vector3) {
        if (entity !is EntityPrimedTNT) {
            super.addVelocityToEntity(entity, vector)
        }
    }
}