package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockConduit @PowerNukkitOnly constructor() : BlockTransparent(), BlockEntityHolder<BlockEntityConduit?> {
    @get:Override
    override val id: Int
        get() = CONDUIT

    @get:Override
    override val name: String
        get() = "Conduit"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityConduit?>
        get() = BlockEntityConduit::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.CONDUIT

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @Override
    fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (item.getBlock() != null && item.getBlockId() === CONDUIT && target.getId() === CONDUIT) {
            return false
        }
        val conduit: BlockEntityConduit = BlockEntityHolder.setBlockAndCreateEntity(this, true, true,
                CompoundTag().putBoolean("IsMovable", true))
        if (conduit != null) {
            conduit.scheduleUpdate()
            return true
        }
        return false
    }

    @get:Override
    override val lightLevel: Int
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIAMOND_BLOCK_COLOR

    @get:Override
    override val minX: Double
        get() = x + 5.0 / 16

    @get:Override
    override val minY: Double
        get() = y + 5.0 / 16

    @get:Override
    override val minZ: Double
        get() = z + 5.0 / 16

    @get:Override
    override val maxX: Double
        get() = x + 11.0 / 16

    @get:Override
    override val maxY: Double
        get() = y + 11.0 / 16

    @get:Override
    override val maxZ: Double
        get() = z + 11.0 / 16
}