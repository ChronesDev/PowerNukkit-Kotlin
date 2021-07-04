package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockFurnaceBurning @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), Faceable, BlockEntityHolder<BlockEntityFurnace?> {
    @get:Override
    override val id: Int
        get() = BURNING_FURNACE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Burning Furnace"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityFurnace?>
        get() = BlockEntityFurnace::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.FURNACE

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val resistance: Double
        get() = 17.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val lightLevel: Int
        get() = 13

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val faces = intArrayOf(2, 5, 3, 4)
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        val nbt: CompoundTag = CompoundTag().putList(ListTag("Items"))
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName())
        }
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val furnace: BlockEntityFurnace = getOrCreateBlockEntity()
        if (furnace.namedTag.contains("Lock") && furnace.namedTag.get("Lock") is StringTag
                && !furnace.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false
        }
        player.addWindow(furnace.getInventory())
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.FURNACE))
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityFurnace = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else super.getComparatorInputOverride()
        }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x7)

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}