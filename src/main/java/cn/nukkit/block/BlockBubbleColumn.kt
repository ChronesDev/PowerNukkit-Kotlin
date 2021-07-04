package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBubbleColumn @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BUBBLE_COLUMN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Bubble Column"

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return Item.EMPTY_ARRAY
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockAir())
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBePlaced(): Boolean {
        return false
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB? {
        return null
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        if (entity.canBeMovedByCurrents()) {
            if (up().getId() === AIR) {
                if (getDamage() === 1) {
                    entity.motionY = Math.max(-0.9, entity.motionY - 0.03)
                } else {
                    entity.motionY = Math.min(1.8, entity.motionY + 0.1)
                }
                val random: ThreadLocalRandom = ThreadLocalRandom.current()
                for (i in 0..1) {
                    level.addParticle(SplashParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())))
                    level.addParticle(BubbleParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())))
                }
            } else {
                if (getDamage() === 1) {
                    entity.motionY = Math.max(-0.3, entity.motionY - 0.3)
                } else {
                    entity.motionY = Math.min(0.7, entity.motionY + 0.06)
                }
            }
            entity.motionChanged = true
            entity.resetFallDistance()
        }
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (down().getId() === MAGMA) {
            setDamage(1)
        }
        this.getLevel().setBlock(this, 1, BlockWater(), true, false)
        this.getLevel().setBlock(this, this, true, true)
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 100

    @get:Override
    override val resistance: Double
        get() = 500

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val water: Block = getLevelBlockAtLayer(1)
            if (water !is BlockWater || water.getDamage() !== 0 && water.getDamage() !== 8) {
                fadeOut(water)
                return type
            }
            if (water.getDamage() === 8) {
                water.setDamage(0)
                this.getLevel().setBlock(this, 1, water, true, false)
            }
            val down: Block = down()
            if (down.getId() === BUBBLE_COLUMN) {
                if (down.getDamage() !== this.getDamage()) {
                    this.getLevel().setBlock(this, down, true, true)
                }
            } else if (down.getId() === MAGMA) {
                if (this.getDamage() !== 1) {
                    setDamage(1)
                    this.getLevel().setBlock(this, this, true, true)
                }
            } else if (down.getId() === SOUL_SAND) {
                if (this.getDamage() !== 0) {
                    setDamage(0)
                    this.getLevel().setBlock(this, this, true, true)
                }
            } else {
                fadeOut(water)
                return type
            }
            val up: Block = up()
            if (up is BlockWater && (up.getDamage() === 0 || up.getDamage() === 8)) {
                val event = BlockFromToEvent(this, up)
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(up, 1, BlockWater(), true, false)
                    this.getLevel().setBlock(up, 0, BlockBubbleColumn(this.getDamage()), true, true)
                }
            }
            return type
        }
        return 0
    }

    private fun fadeOut(water: Block) {
        val event = BlockFadeEvent(this, water.clone())
        if (!event.isCancelled()) {
            this.getLevel().setBlock(this, 1, BlockAir(), true, false)
            this.getLevel().setBlock(this, 0, event.getNewState(), true, true)
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val DRAG_DOWN: BooleanBlockProperty = BooleanBlockProperty("drag_down", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DRAG_DOWN)
    }
}