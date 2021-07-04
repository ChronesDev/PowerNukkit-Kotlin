package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockScaffolding : BlockFallableMeta {
    @PowerNukkitOnly
    constructor() {
        // Does nothing
    }

    @PowerNukkitOnly
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = SCAFFOLDING

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Scaffolding"

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var stability: Int
        get() = getDamage() and 0x7
        set(stability) {
            setDamage(stability and 0x7 or (getDamage() and 0x8))
        }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var stabilityCheck: Boolean
        get() = getDamage() and 0x8 > 0
        set(check) {
            if (check) {
                setDamage(getDamage() or 0x8)
            } else {
                setDamage(getDamage() and 0x7)
            }
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockScaffolding(0))
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (block is BlockLava) {
            return false
        }
        val down: Block = down()
        if (target.getId() !== SCAFFOLDING && down.getId() !== SCAFFOLDING && down.getId() !== AIR && !down.isSolid()) {
            var scaffoldOnSide = false
            for (i in 0..3) {
                val sideFace: BlockFace = BlockFace.fromHorizontalIndex(i)
                if (sideFace !== face) {
                    val side: Block = getSide(sideFace)
                    if (side.getId() === SCAFFOLDING) {
                        scaffoldOnSide = true
                        break
                    }
                }
            }
            if (!scaffoldOnSide) {
                return false
            }
        }
        setDamage(0x8)
        this.getLevel().setBlock(this, this, true, true)
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = down()
            if (down.isSolid()) {
                if (getDamage() !== 0) {
                    setDamage(0)
                    this.getLevel().setBlock(this, this, true, true)
                }
                return type
            }
            var stability = 7
            for (face in BlockFace.values()) {
                if (face === BlockFace.UP) {
                    continue
                }
                val otherBlock: Block = getSide(face)
                if (otherBlock.getId() === SCAFFOLDING) {
                    val other = otherBlock as BlockScaffolding
                    val otherStability = other.stability
                    if (otherStability < stability) {
                        stability = if (face === BlockFace.DOWN) {
                            otherStability
                        } else {
                            otherStability + 1
                        }
                    }
                }
            }
            if (stability >= 7) {
                if (stabilityCheck) {
                    super.onUpdate(type)
                } else {
                    this.getLevel().scheduleUpdate(this, 0)
                }
                return type
            }
            stabilityCheck = false
            stability = stability
            this.getLevel().setBlock(this, this, true, true)
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.getLevel().useBreakOn(this)
            return type
        }
        return 0
    }

    @Override
    protected override fun createFallingEntity(customNbt: CompoundTag): EntityFallingBlock {
        setDamage(0)
        customNbt.putBoolean("BreakOnLava", true)
        return super.createFallingEntity(customNbt)
    }

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 0

    @get:Override
    override val burnChance: Int
        get() = 60

    @get:Override
    override val burnAbility: Int
        get() = 60

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun canBeClimbed(): Boolean {
        return true
    }

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(x, y + 2.0 / 16, z, x + 1, y + 1, z + 1)
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.resetFallDistance()
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val boundingBox: AxisAlignedBB
        get() = this

    @get:Override
    override val collisionBoundingBox: AxisAlignedBB
        get() = this

    @get:Override
    override val minY: Double
        get() = this.y + 14.0 / 16

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @get:Override
    override val isTransparent: Boolean
        get() = true

    @get:Override
    override val color: BlockColor
        get() = BlockColor.TRANSPARENT_BLOCK_COLOR

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace): Boolean {
        return side === BlockFace.UP
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val STABILITY_CHECK: BooleanBlockProperty = BooleanBlockProperty("stability_check", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val STABILITY: IntBlockProperty = IntBlockProperty("stability", false, 7)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STABILITY, STABILITY_CHECK)
    }
}