package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockChest @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntityChest?> {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityChest?>
        get() = BlockEntityChest::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.CHEST

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val id: Int
        get() = CHEST

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Chest"

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val minX: Double
        get() = this.x + 0.0625

    @get:Override
    override val minY: Double
        get() = this.y

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
        var chest: BlockEntityChest? = null
        val faces = intArrayOf(2, 5, 3, 4)
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        for (side in 2..5) {
            if ((this.getDamage() === 4 || this.getDamage() === 5) && (side == 4 || side == 5)) {
                continue
            } else if ((this.getDamage() === 3 || this.getDamage() === 2) && (side == 2 || side == 3)) {
                continue
            }
            val c: Block = this.getSide(BlockFace.fromIndex(side))
            if (c is BlockChest && c.getDamage() === this.getDamage()) {
                val blockEntity: BlockEntity = this.getLevel().getBlockEntity(c)
                if (blockEntity is BlockEntityChest && !(blockEntity as BlockEntityChest).isPaired()) {
                    chest = blockEntity as BlockEntityChest
                    break
                }
            }
        }
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
        val blockEntity: BlockEntityChest = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt)
                ?: return false
        if (chest != null) {
            chest.pairWith(blockEntity)
            blockEntity.pairWith(chest)
        }
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val chest: BlockEntityChest = getBlockEntity()
        if (chest != null) {
            chest.unpair()
        }
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true, true)
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val top: Block = up()
        if (!top.isTransparent()) {
            return false
        }
        val chest: BlockEntityChest = getOrCreateBlockEntity()
        if (chest.namedTag.contains("Lock") && chest.namedTag.get("Lock") is StringTag
                && !chest.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false
        }
        player.addWindow(chest.getInventory())
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityChest = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else super.getComparatorInputOverride()
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
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