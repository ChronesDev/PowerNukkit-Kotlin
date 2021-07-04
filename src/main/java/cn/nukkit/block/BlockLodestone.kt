package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Log4j2
class BlockLodestone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid(), BlockEntityHolder<BlockEntityLodestone?> {
    @get:Override
    override val id: Int
        get() = LODESTONE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityLodestone?>
        get() = BlockEntityLodestone::class.java

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityType: String
        get() = BlockEntity.LODESTONE

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (player == null || item.isNull() || item.getId() !== ItemID.COMPASS && item.getId() !== ItemID.LODESTONE_COMPASS) {
            return false
        }
        val compass: ItemCompassLodestone = Item.get(ItemID.LODESTONE_COMPASS) as ItemCompassLodestone
        if (item.hasCompoundTag()) {
            compass.setCompoundTag(item.getCompoundTag().clone())
        }
        val trackingHandle: Int
        try {
            trackingHandle = getOrCreateBlockEntity().requestTrackingHandler()
            compass.setTrackingHandle(trackingHandle)
        } catch (e: Exception) {
            log.warn("Could not create a lodestone compass to {} for {}", getLocation(), player.getName(), e)
            return false
        }
        var added = true
        if (item.getCount() === 1) {
            player.getInventory().setItemInHand(compass)
        } else {
            val clone: Item = item.clone()
            clone.count--
            player.getInventory().setItemInHand(clone)
            for (failed in player.getInventory().addItem(compass)) {
                added = false
                player.getLevel().dropItem(player.getPosition(), failed)
            }
        }
        getLevel().addSound(player.getPosition(), Sound.LODESTONE_COMPASS_LINK_COMPASS_TO_LODESTONE)
        if (added) {
            try {
                getLevel().getServer().getPositionTrackingService().startTracking(player, trackingHandle, false)
            } catch (e: IOException) {
                log.warn("Failed to make the player {} track {} at {}", player.getName(), trackingHandle, getLocation(), e)
            }
            getLevel().getServer().getScheduler().scheduleTask(null, player::updateTrackingPositions)
        }
        return true
    }

    @get:Override
    override val name: String
        get() = "Lodestone"

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 3.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }
}