package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockEnchantingTable : BlockTransparent(), BlockEntityHolder<BlockEntityEnchantTable?> {
    @get:Override
    override val id: Int
        get() = ENCHANTING_TABLE

    @get:Override
    override val name: String
        get() = "Enchanting Table"

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.ENCHANT_TABLE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityEnchantTable?>
        get() = BlockEntityEnchantTable::class.java

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val resistance: Double
        get() = 6000

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val lightLevel: Int
        get() = 12

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will return the right BB height")
    override val maxY: Double
        get() = getY() + 12 / 16.0

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val nbt = CompoundTag()
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
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (player == null) {
            return true
        }
        val enchantTable: BlockEntityEnchantTable = getOrCreateBlockEntity()
        if (enchantTable.namedTag.contains("Lock") && enchantTable.namedTag.get("Lock") is StringTag
                && !enchantTable.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false
        }
        player.addWindow(EnchantInventory(player.getUIInventory(), this.getLocation()), Player.ENCHANT_WINDOW_ID)
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.RED_BLOCK_COLOR

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }
}