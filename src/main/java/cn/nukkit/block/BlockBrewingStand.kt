package cn.nukkit.block

import cn.nukkit.Player

class BlockBrewingStand @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val name: String
        get() = "Brewing Stand"

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val id: Int
        get() = BREWING_STAND_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val lightLevel: Int
        get() = 1

    @PowerNukkitDifference(info = "Remove placement restrictions, they don't exists in vanilla", since = "1.3.1.2-PN")
    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        getLevel().setBlock(block, this, true, true)
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag("Items"))
                .putString("id", BlockEntity.BREWING_STAND)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName())
        }
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        val brewing: BlockEntityBrewingStand = BlockEntity.createBlockEntity(BlockEntity.BREWING_STAND, getLevel().getChunk(this.x as Int shr 4, this.z as Int shr 4), nbt) as BlockEntityBrewingStand
        return brewing != null
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (player != null) {
            val t: BlockEntity = getLevel().getBlockEntity(this)
            val brewing: BlockEntityBrewingStand
            if (t is BlockEntityBrewingStand) {
                brewing = t as BlockEntityBrewingStand
            } else {
                val nbt: CompoundTag = CompoundTag()
                        .putList(ListTag("Items"))
                        .putString("id", BlockEntity.BREWING_STAND)
                        .putInt("x", this.x as Int)
                        .putInt("y", this.y as Int)
                        .putInt("z", this.z as Int)
                brewing = BlockEntity.createBlockEntity(BlockEntity.BREWING_STAND, this.getLevel().getChunk(this.x as Int shr 4, this.z as Int shr 4), nbt) as BlockEntityBrewingStand
                if (brewing == null) {
                    return false
                }
            }
            if (brewing.namedTag.contains("Lock") && brewing.namedTag.get("Lock") is StringTag) {
                if (!brewing.namedTag.getString("Lock").equals(item.getCustomName())) {
                    return false
                }
            }
            player.addWindow(brewing.getInventory())
        }
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBrewingStand()
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntity = this.level.getBlockEntity(this)
            return if (blockEntity is BlockEntityBrewingStand) {
                ContainerInventory.calculateRedstone((blockEntity as BlockEntityBrewingStand).getInventory())
            } else super.getComparatorInputOverride()
        }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HAS_POTION_A: BooleanBlockProperty = BooleanBlockProperty("brewing_stand_slot_a_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HAS_POTION_B: BooleanBlockProperty = BooleanBlockProperty("brewing_stand_slot_b_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HAS_POTION_C: BooleanBlockProperty = BooleanBlockProperty("brewing_stand_slot_c_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HAS_POTION_A, HAS_POTION_B, HAS_POTION_C)
    }
}