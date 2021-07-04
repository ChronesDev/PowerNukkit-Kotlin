package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockRedstoneTorch @JvmOverloads constructor(meta: Int = 0) : BlockTorch(meta), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "Redstone Torch"

    @get:Override
    override val id: Int
        get() = REDSTONE_TORCH

    @get:Override
    override val lightLevel: Int
        get() = 7

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false
        }
        if (this.level.getServer().isRedstoneEnabled()) {
            if (!checkState()) {
                updateAllAroundRedstone(getBlockFace().getOpposite())
            }
            checkState()
        }
        return true
    }

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return if (getBlockFace() !== side) 15 else 0
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (side === BlockFace.DOWN) getWeakPower(side) else 0
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        if (!super.onBreak(item)) {
            return false
        }
        if (this.level.getServer().isRedstoneEnabled()) {
            updateAllAroundRedstone(getBlockFace().getOpposite())
        }
        return true
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (super.onUpdate(type) === 0) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0
            }
            if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
                this.level.scheduleUpdate(this, tickRate())
            } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
                val ev = RedstoneUpdateEvent(this)
                getLevel().getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return 0
                }
                if (checkState()) {
                    return 1
                }
            }
        }
        return 0
    }

    private fun checkState(): Boolean {
        if (isPoweredFromSide) {
            this.level.setBlock(getLocation(), Block.get(BlockID.UNLIT_REDSTONE_TORCH, getDamage()), false, true)
            updateAllAroundRedstone(getBlockFace().getOpposite())
            return true
        }
        return false
    }

    @get:PowerNukkitDifference(info = "Check if the side block is piston and if piston is getting power.", since = "1.4.0.0-PN")
    protected val isPoweredFromSide: Boolean
        protected get() {
            val face: BlockFace = getBlockFace().getOpposite()
            return if (this.getSide(face) is BlockPistonBase && this.getSide(face).isGettingPower()) {
                true
            } else this.level.isSidePowered(this.getLocation().getSide(face), face)
        }

    @Override
    override fun tickRate(): Int {
        return 2
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR
}