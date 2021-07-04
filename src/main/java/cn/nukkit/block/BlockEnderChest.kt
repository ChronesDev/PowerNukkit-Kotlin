package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockEnderChest @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntityEnderChest?> {
    val viewers: Set<Player> = HashSet()

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val id: Int
        get() = ENDER_CHEST

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.ENDER_CHEST

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityEnderChest?>
        get() = BlockEntityEnderChest::class.java

    @get:Override
    override val lightLevel: Int
        get() = 7

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val name: String
        get() = "Chest"

    @get:Override
    override val hardness: Double
        get() = 22.5

    @get:Override
    override val resistance: Double
        get() = 3000

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val minX: Double
        get() = this.x + 0.0625

    @get:Override
    override val minZ: Double
        get() = this.z + 0.0625

    @get:Override
    override val maxX: Double
        get() = this.x + 0.9375

    @get:Override
    override val maxY: Double
        get() = this.y + 0.9475

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.9375

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val faces = intArrayOf(2, 5, 3, 4)
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
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
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val top: Block = this.up()
        if (!top.isTransparent()) {
            return false
        }
        val chest: BlockEntityEnderChest = getOrCreateBlockEntity()
        if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") is StringTag
                && !chest.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false
        }
        player.setViewingEnderChest(this)
        player.addWindow(player.getEnderChestInventory())
        return true
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= toolTier) {
            arrayOf<Item>(
                    Item.get(Item.OBSIDIAN, 0, 8)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.OBSIDIAN_BLOCK_COLOR

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @get:Override
    @get:Nullable
    override val blockEntity: E?
        get() = getTypedBlockEntity(BlockEntityEnderChest::class.java)

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockChest.PROPERTIES
    }
}