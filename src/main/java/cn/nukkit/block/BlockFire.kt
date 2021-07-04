package cn.nukkit.block

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockFire @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = FIRE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val name: String
        get() = "Fire Block"

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (!entity.hasEffect(Effect.FIRE_RESISTANCE)) {
            entity.attack(EntityDamageByBlockEvent(this, entity, DamageCause.FIRE, 1))
        }
        val ev = EntityCombustByBlockEvent(this, entity, 8)
        if (entity is EntityArrow) {
            ev.setCancelled()
        }
        Server.getInstance().getPluginManager().callEvent(ev)
        if (!ev.isCancelled() && entity.isAlive() && entity.noDamageTicks === 0) {
            entity.setOnFire(ev.getDuration())
        }
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @PowerNukkitDifference(info = "Soul Fire Implementation", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_RANDOM) {
            val down: Block = down()
            if (type == Level.BLOCK_UPDATE_NORMAL) {
                val downId: Int = down.getId()
                if (downId == Block.SOUL_SAND || downId == Block.SOUL_SOIL) {
                    this.getLevel().setBlock(this, getCurrentState().withBlockId(BlockID.SOUL_FIRE).getBlock(this))
                    return type
                }
            }
            if (!isBlockTopFacingSurfaceSolid(down) && !canNeighborBurn()) {
                val event = BlockFadeEvent(this, get(AIR))
                level.getServer().getPluginManager().callEvent(event)
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true)
                }
            } else if (this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK) && !level.isUpdateScheduled(this, this)) {
                level.scheduleUpdate(this, tickRate())
            }
            return Level.BLOCK_UPDATE_NORMAL
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED && this.level.gameRules.getBoolean(GameRule.DO_FIRE_TICK)) {
            val down: Block = down()
            val downId: Int = down.getId()
            val forever = downId == BlockID.NETHERRACK || downId == BlockID.MAGMA || downId == BlockID.BEDROCK && (down as BlockBedrock).getBurnIndefinitely()
            val random: ThreadLocalRandom = ThreadLocalRandom.current()

            //TODO: END
            if (!isBlockTopFacingSurfaceSolid(down) && !canNeighborBurn()) {
                val event = BlockFadeEvent(this, get(AIR))
                level.getServer().getPluginManager().callEvent(event)
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true)
                }
            }
            if (!forever && this.getLevel().isRaining() &&
                    (this.getLevel().canBlockSeeSky(this) ||
                            this.getLevel().canBlockSeeSky(this.east()) ||
                            this.getLevel().canBlockSeeSky(this.west()) ||
                            this.getLevel().canBlockSeeSky(this.south()) ||
                            this.getLevel().canBlockSeeSky(this.north()))) {
                val event = BlockFadeEvent(this, get(AIR))
                level.getServer().getPluginManager().callEvent(event)
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true)
                }
            } else {
                val meta: Int = this.getDamage()
                if (meta < 15) {
                    val newMeta: Int = meta + random.nextInt(3)
                    this.setDamage(Math.min(newMeta, 15))
                    this.getLevel().setBlock(this, this, true)
                }
                this.getLevel().scheduleUpdate(this, tickRate() + random.nextInt(10))
                if (!forever && !canNeighborBurn()) {
                    if (!isBlockTopFacingSurfaceSolid(down) || meta > 3) {
                        val event = BlockFadeEvent(this, get(AIR))
                        level.getServer().getPluginManager().callEvent(event)
                        if (!event.isCancelled()) {
                            level.setBlock(this, event.getNewState(), true)
                        }
                    }
                } else if (!forever && down.getBurnAbility() <= 0 && meta == 15 && random.nextInt(4) === 0) {
                    val event = BlockFadeEvent(this, get(AIR))
                    level.getServer().getPluginManager().callEvent(event)
                    if (!event.isCancelled()) {
                        level.setBlock(this, event.getNewState(), true)
                    }
                } else {
                    val o = 0

                    //TODO: decrease the o if the rainfall values are high
                    tryToCatchBlockOnFire(this.east(), 300 + o, meta)
                    tryToCatchBlockOnFire(this.west(), 300 + o, meta)
                    tryToCatchBlockOnFire(down, 250 + o, meta)
                    tryToCatchBlockOnFire(this.up(), 250 + o, meta)
                    tryToCatchBlockOnFire(this.south(), 300 + o, meta)
                    tryToCatchBlockOnFire(this.north(), 300 + o, meta)
                    for (x in (this.x - 1) as Int..(this.x + 1) as Int) {
                        for (z in (this.z - 1) as Int..(this.z + 1) as Int) {
                            for (y in (this.y - 1) as Int..(this.y + 4) as Int) {
                                if (x != x || y != y || z != z) {
                                    var k = 100
                                    if (y > y + 1) {
                                        k += (y - (y + 1)) * 100
                                    }
                                    val block: Block = this.getLevel().getBlock(Vector3(x, y, z))
                                    val chance = getChanceOfNeighborsEncouragingFire(block)
                                    if (chance > 0) {
                                        val t: Int = (chance + 40 + this.getLevel().getServer().getDifficulty() * 7) / (meta + 30)

                                        //TODO: decrease the t if the rainfall values are high
                                        if (t > 0 && random.nextInt(k) <= t) {
                                            var damage: Int = meta + random.nextInt(5) / 4
                                            if (damage > 15) {
                                                damage = 15
                                            }
                                            val e = BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD)
                                            this.level.getServer().getPluginManager().callEvent(e)
                                            if (!e.isCancelled()) {
                                                this.getLevel().setBlock(block, Block.get(BlockID.FIRE, damage), true)
                                                this.getLevel().scheduleUpdate(block, tickRate())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0
    }

    private fun tryToCatchBlockOnFire(block: Block, bound: Int, damage: Int) {
        val burnAbility: Int = block.getBurnAbility()
        val random: Random = ThreadLocalRandom.current()
        if (random.nextInt(bound) < burnAbility) {
            if (random.nextInt(damage + 10) < 5) {
                var meta: Int = damage + random.nextInt(5) / 4
                if (meta > 15) {
                    meta = 15
                }
                val e = BlockIgniteEvent(block, this, null, BlockIgniteEvent.BlockIgniteCause.SPREAD)
                this.level.getServer().getPluginManager().callEvent(e)
                if (!e.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(BlockID.FIRE, meta), true)
                    this.getLevel().scheduleUpdate(block, tickRate())
                }
            } else {
                val ev = BlockBurnEvent(block)
                this.getLevel().getServer().getPluginManager().callEvent(ev)
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(block, Block.get(BlockID.AIR), true)
                }
            }
            if (block is BlockTNT) {
                block.prime()
            }
        }
    }

    private fun getChanceOfNeighborsEncouragingFire(block: Block): Int {
        return if (block.getId() !== AIR) {
            0
        } else {
            var chance = 0
            chance = Math.max(chance, block.east().getBurnChance())
            chance = Math.max(chance, block.west().getBurnChance())
            chance = Math.max(chance, block.down().getBurnChance())
            chance = Math.max(chance, block.up().getBurnChance())
            chance = Math.max(chance, block.south().getBurnChance())
            chance = Math.max(chance, block.north().getBurnChance())
            chance
        }
    }

    fun canNeighborBurn(): Boolean {
        for (face in BlockFace.values()) {
            if (this.getSide(face).getBurnChance() > 0) {
                return true
            }
        }
        return false
    }

    fun isBlockTopFacingSurfaceSolid(block: Block?): Boolean {
        if (block != null) {
            if (block.isSolid()) {
                return true
            } else {
                if (block is BlockStairs &&
                        block.getDamage() and 4 === 4) {
                    return true
                } else if (block is BlockSlab &&
                        block.getDamage() and 8 === 8) {
                    return true
                } else if (block is BlockSnowLayer &&
                        block.getDamage() and 7 === 7) {
                    return true
                }
            }
        }
        return false
    }

    @Override
    override fun tickRate(): Int {
        return 30
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.LAVA_BLOCK_COLOR

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val FIRE_AGE: IntBlockProperty = CommonBlockProperties.AGE_15

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FIRE_AGE)
    }
}