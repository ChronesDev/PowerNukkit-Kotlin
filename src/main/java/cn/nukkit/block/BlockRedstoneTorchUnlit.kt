package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 * @since 10.4.2017
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockRedstoneTorchUnlit @JvmOverloads constructor(meta: Int = 0) : BlockTorch(meta), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "Unlit Redstone Torch"

    @get:Override
    override val id: Int
        get() = UNLIT_REDSTONE_TORCH

    @get:Override
    override val lightLevel: Int
        get() = 0

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return 0
    }

    @Override
    override fun getStrongPower(side: BlockFace?): Int {
        return 0
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.REDSTONE_TORCH))
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
        if (!isPoweredFromSide) {
            this.level.setBlock(getLocation(), Block.get(BlockID.REDSTONE_TORCH, getDamage()), false, true)
            updateAllAroundRedstone(getBlockFace().getOpposite())
            return true
        }
        return false
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
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
}