package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockNetherReactor @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid(), BlockEntityHolder<BlockEntityNetherReactor?> {
    @get:Override
    override val id: Int
        get() = NETHER_REACTOR

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.NETHER_REACTOR

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityNetherReactor?>
        get() = BlockEntityNetherReactor::class.java

    @get:Override
    override val name: String
        get() = "Nether Reactor Core"

    @get:Override
    override val hardness: Double
        get() = 10

    @get:Override
    override val resistance: Double
        get() = 6

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe()) {
            arrayOf<Item>(
                    Item.get(ItemID.DIAMOND, 0, 3),
                    Item.get(ItemID.IRON_INGOT, 0, 6)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR
}