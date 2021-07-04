package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
class BlockCactus @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val id: Int
        get() = CACTUS

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val resistance: Double
        get() = 2

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val minX: Double
        get() = this.x + 0.0625

    @get:Override
    override val minY: Double
        get() = this.y

    @get:Override
    override val minZ: Double
        get() = this.z + 0.0625

    @get:Override
    override val maxX: Double
        get() = this.x + 0.9375

    @get:Override
    override val maxY: Double
        get() = this.y + 0.9375

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.9375

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1)
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.attack(EntityDamageByBlockEvent(this, entity, DamageCause.CONTACT, 1))
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = down()
            if (down.getId() !== SAND && down.getId() !== CACTUS) {
                this.getLevel().useBreakOn(this)
            } else {
                for (side in 2..5) {
                    val block: Block = getSide(BlockFace.fromIndex(side))
                    if (!block.canBeFlowedInto()) {
                        this.getLevel().useBreakOn(this)
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (down().getId() !== CACTUS) {
                if (this.getDamage() === 0x0F) {
                    for (y in 1..2) {
                        val b: Block = this.getLevel().getBlock(Vector3(this.x, y + y, this.z))
                        if (b.getId() === AIR) {
                            val event = BlockGrowEvent(b, Block.get(BlockID.CACTUS))
                            Server.getInstance().getPluginManager().callEvent(event)
                            if (!event.isCancelled()) {
                                this.getLevel().setBlock(b, event.getNewState(), true)
                            }
                        }
                    }
                    this.setDamage(0)
                    this.getLevel().setBlock(this, this)
                } else {
                    this.setDamage(this.getDamage() + 1)
                    this.getLevel().setBlock(this, this)
                }
            }
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val down: Block = this.down()
        if (down.getId() === SAND || down.getId() === CACTUS) {
            val block0: Block = north()
            val block1: Block = south()
            val block2: Block = west()
            val block3: Block = east()
            if (block0.canBeFlowedInto() && block1.canBeFlowedInto() && block2.canBeFlowedInto() && block3.canBeFlowedInto()) {
                this.getLevel().setBlock(this, this, true)
                return true
            }
        }
        return false
    }

    @get:Override
    override val name: String
        get() = "Cactus"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                Item.getBlock(BlockID.CACTUS, 0, 1)
        )
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(CommonBlockProperties.AGE_15)
    }
}