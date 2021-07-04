package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
abstract class BlockRedstoneDiode @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), RedstoneComponent, Faceable {
    protected var isPowered = false

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val pos: Vector3 = getLocation()
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true)
        if (this.level.getServer().isRedstoneEnabled()) {
            updateAllAroundRedstone()
        }
        return true
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!isSupportValid(down())) {
            return false
        }
        this.setDamage(if (player != null) player.getDirection().getOpposite().getHorizontalIndex() else 0)
        if (!this.level.setBlock(block, this, true, true)) {
            return false
        }
        if (this.level.getServer().isRedstoneEnabled()) {
            if (shouldBePowered()) {
                this.level.scheduleUpdate(this, 1)
            }
        }
        return true
    }

    protected fun isSupportValid(support: Block): Boolean {
        return BlockLever.isSupportValid(support, BlockFace.UP) || support is BlockCauldron
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0
            }
            if (!isLocked) {
                val pos: Vector3 = getLocation()
                val shouldBePowered = shouldBePowered()
                if (isPowered && !shouldBePowered) {
                    this.level.setBlock(pos, unpowered, true, true)
                    val side: Block = this.getSide(facing.getOpposite())
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE)
                    RedstoneComponent.updateAroundRedstone(side)
                } else if (!isPowered) {
                    this.level.setBlock(pos, getPowered(), true, true)
                    val side: Block = this.getSide(facing.getOpposite())
                    side.onUpdate(Level.BLOCK_UPDATE_REDSTONE)
                    RedstoneComponent.updateAroundRedstone(side)
                    if (!shouldBePowered) {
                        level.scheduleUpdate(getPowered(), this, delay)
                    }
                }
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            if (type == Level.BLOCK_UPDATE_NORMAL && !isSupportValid(down())) {
                this.level.useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            } else if (this.level.getServer().isRedstoneEnabled()) {
                // Redstone event
                val ev = RedstoneUpdateEvent(this)
                getLevel().getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return 0
                }
                updateState()
                return type
            }
        }
        return 0
    }

    fun updateState() {
        if (!isLocked) {
            val shouldPowered = shouldBePowered()
            if ((isPowered && !shouldPowered || !isPowered && shouldPowered) && !this.level.isBlockTickPending(this, this)) {
                /*int priority = -1;

                if (this.isFacingTowardsRepeater()) {
                    priority = -3;
                } else if (this.isPowered) {
                    priority = -2;
                }*/
                this.level.scheduleUpdate(this, this, delay)
            }
        }
    }

    val isLocked: Boolean
        get() = false

    protected fun calculateInputStrength(): Int {
        val face: BlockFace = facing
        val pos: Vector3 = this.getLocation().getSide(face)
        val power: Int = this.level.getRedstonePower(pos, face)
        return if (power >= 15) {
            power
        } else {
            val block: Block = this.level.getBlock(pos)
            Math.max(power, if (block.getId() === Block.REDSTONE_WIRE) block.getDamage() else 0)
        }
    }

    protected val powerOnSides: Int
        protected get() {
            val pos: Vector3 = getLocation()
            val face: BlockFace = facing
            val face1: BlockFace = face.rotateY()
            val face2: BlockFace = face.rotateYCCW()
            return Math.max(getPowerOnSide(pos.getSide(face1), face1), getPowerOnSide(pos.getSide(face2), face2))
        }

    protected fun getPowerOnSide(pos: Vector3?, side: BlockFace?): Int {
        val block: Block = this.level.getBlock(pos)
        return if (isAlternateInput(block)) if (block.getId() === Block.REDSTONE_BLOCK) 15 else if (block.getId() === Block.REDSTONE_WIRE) block.getDamage() else this.level.getStrongPower(pos, side) else 0
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    protected fun shouldBePowered(): Boolean {
        return calculateInputStrength() > 0
    }

    abstract val facing: BlockFace
    protected abstract val delay: Int
    protected abstract val unpowered: cn.nukkit.block.Block?
    protected abstract fun getPowered(): Block?

    @get:Override
    override val maxY: Double
        get() = this.y + 0.125

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    protected fun isAlternateInput(block: Block): Boolean {
        return block.isPowerSource()
    }

    protected val redstoneSignal: Int
        protected get() = 15

    override fun getStrongPower(side: BlockFace): Int {
        return getWeakPower(side)
    }

    override fun getWeakPower(side: BlockFace): Int {
        return if (!isPowered()) 0 else if (facing === side) redstoneSignal else 0
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    fun isPowered(): Boolean {
        return isPowered
    }

    val isFacingTowardsRepeater: Boolean
        get() {
            val side: BlockFace = facing.getOpposite()
            val block: Block = this.getSide(side)
            return block is BlockRedstoneDiode && block.facing !== side
        }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return SimpleAxisAlignedBB(this.x, this.y, this.z, this.x + 1, this.y + 0.125, this.z + 1)
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    companion object {
        fun isDiode(block: Block?): Boolean {
            return block is BlockRedstoneDiode
        }
    }
}