package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class BlockTripWire @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val id: Int
        get() = TRIPWIRE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Tripwire"

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @get:Override
    override val resistance: Double
        get() = 0

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @Override
    override fun toItem(): Item {
        return ItemString()
    }

    var isPowered: Boolean
        get() = this.getDamage() and 1 > 0
        set(value) {
            if (value xor isPowered) {
                this.setDamage(this.getDamage() xor 0x01)
            }
        }
    var isAttached: Boolean
        get() = this.getDamage() and 4 > 0
        set(value) {
            if (value xor isAttached) {
                this.setDamage(this.getDamage() xor 0x04)
            }
        }
    var isDisarmed: Boolean
        get() = this.getDamage() and 8 > 0
        set(value) {
            if (value xor isDisarmed) {
                this.setDamage(this.getDamage() xor 0x08)
            }
        }

    @PowerNukkitDifference(info = "Trigger observer.", since = "1.4.0.0-PN")
    @Override
    override fun onEntityCollide(entity: Entity) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return
        }
        if (!entity.doesTriggerPressurePlate()) {
            return
        }
        val powered = isPowered
        if (!powered) {
            isPowered = true
            this.level.setBlock(this, this, true, false)
            updateHook(false)
            this.level.scheduleUpdate(this, 10)
            this.level.updateComparatorOutputLevelSelective(this, true)
        }
    }

    private fun updateHook(scheduleUpdate: Boolean) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return
        }
        for (side in arrayOf<BlockFace>(BlockFace.SOUTH, BlockFace.WEST)) {
            for (i in 1..41) {
                val block: Block = this.getSide(side, i)
                if (block is BlockTripWireHook) {
                    val hook: BlockTripWireHook = block
                    if (hook.getFacing() === side.getOpposite()) {
                        hook.calculateState(false, true, i, this)
                    }

                    /*if(scheduleUpdate) {
                        this.level.scheduleUpdate(hook, 10);
                    }*/break
                }
                if (block.getId() !== Block.TRIPWIRE) {
                    break
                }
            }
        }
    }

    @PowerNukkitDifference(info = "Trigger observer.", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isPowered) {
                return type
            }
            var found = false
            for (entity in this.level.getCollidingEntities(this.getCollisionBoundingBox())) {
                if (!entity.doesTriggerPressurePlate()) {
                    continue
                }
                found = true
            }
            if (found) {
                this.level.scheduleUpdate(this, 10)
            } else {
                isPowered = false
                this.level.setBlock(this, this, true, false)
                updateHook(false)
                this.level.updateComparatorOutputLevelSelective(this, true)
            }
            return type
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        this.getLevel().setBlock(this, this, true, true)
        updateHook(false)
        return true
    }

    @Override
    override fun onBreak(item: Item): Boolean {
        if (item.getId() === Item.SHEARS) {
            isDisarmed = true
            this.level.setBlock(this, this, true, false)
            updateHook(false)
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
        } else {
            isPowered = true
            this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
            updateHook(true)
        }
        return true
    }

    @get:Override
    override val maxY: Double
        get() = this.y + 0.5

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return this
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val ATTACHED: BooleanBlockProperty = BooleanBlockProperty("attached_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val DISARMED: BooleanBlockProperty = BooleanBlockProperty("disarmed_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val SUSPENDED: BooleanBlockProperty = BooleanBlockProperty("suspended_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(POWERED, SUSPENDED, ATTACHED, DISARMED)
    }
}