package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Leonidius20, joserobjr
 * @since 18.08.18
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockObserver @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), RedstoneComponent, Faceable {
    @get:Override
    override val name: String
        get() = "Observer"

    @get:Override
    override val id: Int
        get() = OBSERVER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (player != null) {
            blockFace = if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                val y: Double = player.y + player.getEyeHeight()
                if (y - y > 2) {
                    BlockFace.DOWN
                } else if (y - y > 0) {
                    BlockFace.UP
                } else {
                    player.getHorizontalFacing()
                }
            } else {
                player.getHorizontalFacing()
            }
        }
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    override val isPowerSource: Boolean
        get() = true

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (isPowered && side === blockFace) 15 else 0
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    override fun getWeakPower(face: BlockFace): Int {
        return getStrongPower(face)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implemented")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            val ev = RedstoneUpdateEvent(this)
            val pluginManager: PluginManager = level.getServer().getPluginManager()
            pluginManager.callEvent(ev)
            if (ev.isCancelled()) {
                return 0
            }
            if (!isPowered) {
                level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 0, 15))
                isPowered = true
                if (level.setBlock(this, this)) {
                    getSide(blockFace.getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE)
                    RedstoneComponent.updateAroundRedstone(getSide(blockFace.getOpposite()))
                    level.scheduleUpdate(this, 2)
                }
            } else {
                pluginManager.callEvent(BlockRedstoneEvent(this, 15, 0))
                isPowered = false
                level.setBlock(this, this)
                getSide(blockFace.getOpposite()).onUpdate(Level.BLOCK_UPDATE_REDSTONE)
                RedstoneComponent.updateAroundRedstone(getSide(blockFace.getOpposite()))
            }
            return Level.BLOCK_UPDATE_SCHEDULED
        } else if (type == Level.BLOCK_UPDATE_MOVED) {
            onNeighborChange(blockFace)
            return type
        }
        return 0
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onNeighborChange(@Nonnull side: BlockFace) {
        val server: Server = level.getServer()
        val blockFace: BlockFace = blockFace
        if (!server.isRedstoneEnabled() || isPowered || side !== blockFace || level.isUpdateScheduled(this, this)) {
            return
        }
        val ev = RedstoneUpdateEvent(this)
        server.getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return
        }
        level.scheduleUpdate(this, 5)
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val resistance: Double
        get() = 17.5

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isPowered: Boolean
        get() = getBooleanValue(POWERED)
        set(powered) {
            setBooleanValue(POWERED, powered)
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

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, POWERED)
    }
}