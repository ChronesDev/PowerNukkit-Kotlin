package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/11
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
abstract class BlockPressurePlateBase protected constructor(meta: Int = 0) : BlockFlowable(meta), RedstoneComponent {
    protected var onPitch = 0f
    protected var offPitch = 0f

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val minX: Double
        get() = this.x + 0.625

    @get:Override
    override val minZ: Double
        get() = this.z + 0.625

    @get:Override
    override val minY: Double
        get() = this.y + 0

    @get:Override
    override val maxX: Double
        get() = this.x + 0.9375

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.9375

    @get:Override
    override val maxY: Double
        get() = if (isActivated) this.y + 0.03125 else this.y + 0.0625

    @get:Override
    override val isPowerSource: Boolean
        get() = true
    val isActivated: Boolean
        get() = redstonePower == 0

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
                this.level.useBreakOn(this, ItemTool.getBestTool(getToolType()))
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            val power = redstonePower
            if (power > 0) {
                updateState(power)
            }
        }
        return 0
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return false
        }
        this.level.setBlock(block, this, true, true)
        return true
    }

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(this.x + 0.125, this.y, this.z + 0.125, this.x + 0.875, this.y + 0.25, this.z + 0.875)
    }

    @Override
    override fun onEntityCollide(entity: Entity?) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return
        }
        val power = redstonePower
        if (power == 0) {
            val ev: Event
            if (entity is Player) {
                ev = PlayerInteractEvent(entity as Player?, null, this, null, Action.PHYSICAL)
            } else {
                ev = EntityInteractEvent(entity, this)
            }
            this.level.getServer().getPluginManager().callEvent(ev)
            if (!ev.isCancelled()) {
                updateState(power)
            }
        }
    }

    protected fun updateState(oldStrength: Int) {
        val strength = computeRedstoneStrength()
        val wasPowered = oldStrength > 0
        val isPowered = strength > 0
        if (oldStrength != strength) {
            redstonePower = strength
            this.level.setBlock(this, this, false, false)
            updateAroundRedstone()
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN))
            if (!isPowered && wasPowered) {
                playOffSound()
                this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 15, 0))
            } else if (isPowered && !wasPowered) {
                playOnSound()
                this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 0, 15))
            }
        }
        if (isPowered) {
            this.level.scheduleUpdate(this, 20)
        }
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true)
        if (redstonePower > 0) {
            updateAroundRedstone()
            RedstoneComponent.updateAroundRedstone(this.getSide(BlockFace.DOWN))
        }
        return true
    }

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return redstonePower
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (side === BlockFace.UP) redstonePower else 0
    }

    var redstonePower: Int
        get() = getPropertyValue(REDSTONE_SIGNAL)
        set(power) {
            setIntValue(REDSTONE_SIGNAL, power)
        }

    protected fun playOnSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), LevelSoundEventPacket.SOUND_POWER_ON, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()))
    }

    protected fun playOffSound() {
        this.level.addLevelSoundEvent(this.add(0.5, 0.1, 0.5), LevelSoundEventPacket.SOUND_POWER_OFF, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()))
    }

    protected abstract fun computeRedstoneStrength(): Int

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.REDSTONE_SIGNAL_BLOCK_PROPERTY
    }
}