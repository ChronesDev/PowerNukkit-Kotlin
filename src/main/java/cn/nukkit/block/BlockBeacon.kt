package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockBeacon : BlockTransparent(), BlockEntityHolder<BlockEntityBeacon?> {
    @get:Override
    override val id: Int
        get() = BEACON

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBeacon?>
        get() = BlockEntityBeacon::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BEACON

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val lightLevel: Int
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Beacon"

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        if (player == null) {
            return false
        }
        getOrCreateBlockEntity()
        player.addWindow(BeaconInventory(player.getUIInventory(), this), Player.BEACON_WINDOW_ID)
        return true
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIAMOND_BLOCK_COLOR

    @Override
    override fun canBePulled(): Boolean {
        return false
    }
}