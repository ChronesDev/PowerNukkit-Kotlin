package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBarrel @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta), Faceable, BlockEntityHolder<BlockEntityBarrel?> {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Barrel"

    @get:Override
    override val id: Int
        get() = BARREL

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
        get() = BlockEntity.BARREL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBarrel?>
        get() = BlockEntityBarrel::class.java

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            val y: Double = player.y + player.getEyeHeight()
            if (y - y > 2) {
                this.setDamage(BlockFace.UP.getIndex())
            } else if (y - y > 0) {
                this.setDamage(BlockFace.DOWN.getIndex())
            } else {
                this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
            }
        } else {
            this.setDamage(player.getHorizontalFacing().getOpposite().getIndex())
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
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val barrel: BlockEntityBarrel = getOrCreateBlockEntity()
        if (barrel.namedTag.contains("Lock") && barrel.namedTag.get("Lock") is StringTag
                && !barrel.namedTag.getString("Lock").equals(item.getCustomName())) {
            return false
        }
        player.addWindow(barrel.getInventory())
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockBarrel())
    }

    @get:Override
    @set:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() {
            val index: Int = getDamage() and 0x7
            return BlockFace.fromIndex(index)
        }
        set(face) {
            setDamage(getDamage() and 0x8 or (face.getIndex() and 0x7))
        }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isOpen: Boolean
        get() = getDamage() and 0x8 === 0x8
        set(open) {
            setDamage(getDamage() and 0x7 or if (open) 0x8 else 0x0)
        }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityBarrel = getBlockEntity()
            return if (blockEntity != null) {
                ContainerInventory.calculateRedstone(blockEntity.getInventory())
            } else super.getComparatorInputOverride()
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, OPEN)
    }
}