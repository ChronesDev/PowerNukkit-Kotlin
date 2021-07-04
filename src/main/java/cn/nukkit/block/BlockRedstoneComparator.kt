package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
@Log4j2
abstract class BlockRedstoneComparator @JvmOverloads constructor(meta: Int = 0) : BlockRedstoneDiode(meta), RedstoneComponent, BlockEntityHolder<BlockEntityComparator?> {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityComparator?>
        get() = BlockEntityComparator::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.COMPARATOR

    @get:Override
    protected override val delay: Int
        protected get() = 2

    @get:Override
    override val facing: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage())
    val mode: Mode
        get() = if (getDamage() and 4 > 0) Mode.SUBTRACT else Mode.COMPARE

    @get:Override
    protected override val unpowered: cn.nukkit.block.Block?
        protected get() = Block.get(BlockID.UNPOWERED_COMPARATOR, this.getDamage()) as BlockRedstoneComparator

    @Override
    protected override fun getPowered(): BlockRedstoneComparator {
        return Block.get(BlockID.POWERED_COMPARATOR, this.getDamage()) as BlockRedstoneComparator
    }

    @get:Override
    protected override val redstoneSignal: Int
        protected get() {
            val comparator: BlockEntityComparator = getBlockEntity()
            return if (comparator == null) 0 else comparator.getOutputSignal()
        }

    @Override
    override fun updateState() {
        if (!this.level.isBlockTickPending(this, this)) {
            val output = calculateOutput()
            val power = redstoneSignal
            if (output != power || isPowered() != shouldBePowered()) {
                this.level.scheduleUpdate(this, this, 2)
            }
        }
    }

    protected override fun calculateInputStrength(): Int {
        var power: Int = super.calculateInputStrength()
        val face: BlockFace = facing
        var block: Block = this.getSide(face)
        if (block.hasComparatorInputOverride()) {
            power = block.getComparatorInputOverride()
        } else if (power < 15 && block.isNormalBlock()) {
            block = block.getSide(face)
            if (block.hasComparatorInputOverride()) {
                power = block.getComparatorInputOverride()
            }
        }
        return power
    }

    protected override fun shouldBePowered(): Boolean {
        val input = calculateInputStrength()
        return if (input >= 15) {
            true
        } else if (input == 0) {
            false
        } else {
            val sidePower: Int = this.getPowerOnSides()
            sidePower == 0 || input >= sidePower
        }
    }

    private fun calculateOutput(): Int {
        return if (mode == Mode.SUBTRACT) Math.max(calculateInputStrength() - this.getPowerOnSides(), 0) else calculateInputStrength()
    }

    @PowerNukkitDifference(info = "Trigger observer.", since = "1.4.0.0-PN")
    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (mode == Mode.SUBTRACT) {
            this.setDamage(this.getDamage() - 4)
        } else {
            this.setDamage(this.getDamage() + 4)
        }
        this.level.addLevelEvent(this.add(0.5, 0.5, 0.5), LevelEventPacket.EVENT_SOUND_BUTTON_CLICK, if (mode == Mode.SUBTRACT) 500 else 550)
        this.level.setBlock(this, this, true, false)
        this.level.updateComparatorOutputLevelSelective(this, true)
        //bug?
        onChange()
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            onChange()
            return type
        }
        return super.onUpdate(type)
    }

    @PowerNukkitDifference(info = "Trigger observer.", since = "1.4.0.0-PN")
    private fun onChange() {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return
        }
        val output = calculateOutput()
        // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
        // before the "real" BlockEntity is set. That means, if we'd use the other method here,
        // it would create two BlockEntities.
        val blockEntityComparator: BlockEntityComparator = getBlockEntity() ?: return
        val currentOutput: Int = blockEntityComparator.getOutputSignal()
        blockEntityComparator.setOutputSignal(output)
        if (currentOutput != output || mode == Mode.COMPARE) {
            val shouldBePowered = shouldBePowered()
            val isPowered = isPowered()
            if (isPowered && !shouldBePowered) {
                this.level.setBlock(this, unpowered, true, false)
                this.level.updateComparatorOutputLevelSelective(this, true)
            } else if (!isPowered && shouldBePowered) {
                this.level.setBlock(this, getPowered(), true, false)
                this.level.updateComparatorOutputLevelSelective(this, true)
            }
            val side: Block = this.getSide(facing.getOpposite())
            side.onUpdate(Level.BLOCK_UPDATE_REDSTONE)
            RedstoneComponent.updateAroundRedstone(side)
        }
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val layer0: Block = level.getBlock(this, 0)
        val layer1: Block = level.getBlock(this, 1)
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false
        }
        try {
            createBlockEntity(CompoundTag().putList(ListTag("Items")))
        } catch (e: Exception) {
            log.warn("Failed to create the block entity {} at {}", blockEntityType, getLocation(), e)
            level.setBlock(layer0, 0, layer0, true)
            level.setBlock(layer1, 1, layer1, true)
            return false
        }
        onUpdate(Level.BLOCK_UPDATE_REDSTONE)
        return true
    }

    @Override
    override fun isPowered(): Boolean {
        return this.isPowered || this.getDamage() and 8 > 0
    }

    @Override
    override fun toItem(): Item {
        return ItemRedstoneComparator()
    }

    enum class Mode {
        COMPARE, SUBTRACT
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val OUTPUT_LIT: BooleanBlockProperty = BooleanBlockProperty("output_lit_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val OUTPUT_SUBTRACT: BooleanBlockProperty = BooleanBlockProperty("output_subtract_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, OUTPUT_SUBTRACT, OUTPUT_LIT)
    }
}