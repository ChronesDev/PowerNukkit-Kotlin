package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockHopper @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityHopper?> {
    @get:Override
    override val id: Int
        get() = HOPPER_BLOCK

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
    override val blockEntityClass: Class<out BlockEntityHopper?>
        get() = BlockEntityHopper::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.HOPPER

    @get:Override
    override val name: String
        get() = "Hopper Block"

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 24

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        var facing: BlockFace = face.getOpposite()
        if (facing === BlockFace.UP) {
            facing = BlockFace.DOWN
        }
        blockFace = facing
        if (this.level.getServer().isRedstoneEnabled()) {
            val powered: Boolean = this.isGettingPower()
            if (powered == isEnabled) {
                isEnabled = !powered
            }
        }
        val nbt: CompoundTag = CompoundTag().putList(ListTag("Items"))
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val blockEntity: BlockEntityHopper = getOrCreateBlockEntity()
        return player.addWindow(blockEntity.getInventory()) !== -1
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityHopper = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else super.getComparatorInputOverride()
        }

    @get:DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getBlockFace()", reason = "Duplicated")
    @get:Deprecated
    val facing: BlockFace
        get() = blockFace
    var isEnabled: Boolean
        get() = !getBooleanValue(TOGGLE)
        set(enabled) {
            setBooleanValue(TOGGLE, !enabled)
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            val disabled: Boolean = this.level.isBlockPowered(this.getLocation())
            if (disabled == isEnabled) {
                isEnabled = !disabled
                this.level.setBlock(this, this, false, true)
                val be: BlockEntityHopper = getBlockEntity()
                if (be != null) {
                    be.setDisabled(disabled)
                    if (!disabled) {
                        be.scheduleUpdate()
                    }
                }
            }
            return type
        }
        return 0
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun toItem(): Item {
        return ItemHopper()
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(FACING_DIRECTION)
        set(face) {
            setPropertyValue(FACING_DIRECTION, face)
        }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace): Boolean {
        return side === BlockFace.UP
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, TOGGLE)
    }
}