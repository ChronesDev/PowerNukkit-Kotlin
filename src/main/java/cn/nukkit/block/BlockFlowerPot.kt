package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockFlowerPot @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), BlockEntityHolder<BlockEntityFlowerPot?> {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val name: String
        get() = "Flower Pot"

    @get:Override
    override val id: Int
        get() = FLOWER_POT_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityFlowerPot?>
        get() = BlockEntityFlowerPot::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.FLOWER_POT

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
                level.useBreakOn(this)
                return type
            }
        }
        return 0
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!BlockLever.isSupportValid(down(), BlockFace.UP)) {
            return false
        }
        val nbt: CompoundTag = CompoundTag()
                .putShort("item", 0)
                .putInt("data", 0)
        if (item.hasCustomBlockData()) {
            for (aTag in item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag)
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val flower: Item
        get() {
            val blockEntity: BlockEntityFlowerPot = getBlockEntity() ?: return Item.get(0, 0, 0)
            val id: Int = blockEntity.namedTag.getShort("item")
            if (id == 0) {
                return Item.get(0, 0, 0)
            }
            val data: Int = blockEntity.namedTag.getInt("data")
            return Item.get(id, data, 1)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setFlower(@Nullable item: Item?): Boolean {
        if (item == null || item.getId() === 0) {
            removeFlower()
            return true
        }
        val blockEntity: BlockEntityFlowerPot = getOrCreateBlockEntity()
        var contentId: Int = item.getBlockId()
        if (contentId == -1 || !canPlaceIntoFlowerPot(contentId)) {
            contentId = item.getId()
            if (!canPlaceIntoFlowerPot(contentId)) {
                return false
            }
        }
        val itemMeta: Int = item.getDamage()
        blockEntity.namedTag.putShort("item", contentId)
        blockEntity.namedTag.putInt("data", itemMeta)
        setBooleanValue(UPDATE, true)
        getLevel().setBlock(this, this, true)
        blockEntity.spawnToAll()
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun removeFlower() {
        val blockEntity: BlockEntityFlowerPot = getOrCreateBlockEntity()
        blockEntity.namedTag.putShort("item", 0)
        blockEntity.namedTag.putInt("data", 0)
        setBooleanValue(UPDATE, false)
        getLevel().setBlock(this, this, true)
        blockEntity.spawnToAll()
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (getBooleanValue(UPDATE)) {
            if (player == null) {
                return false
            }
            val flower: Item = flower
            if (flower.getId() !== 0) {
                removeFlower()
                player.giveItem(flower)
                return true
            }
        }
        if (item.isNull()) {
            return false
        }
        val blockEntity: BlockEntityFlowerPot = getOrCreateBlockEntity()
        if (blockEntity.namedTag.getShort("item") !== 0 || blockEntity.namedTag.getInt("mData") !== 0) {
            return false
        }
        if (!setFlower(item)) {
            return false
        }
        if (player == null || !player.isCreative()) {
            item.count--
        }
        return true
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        var dropInside = false
        var insideID = 0
        var insideMeta = 0
        val blockEntity: BlockEntityFlowerPot = getBlockEntity()
        if (blockEntity != null) {
            dropInside = true
            insideID = blockEntity.namedTag.getShort("item")
            insideMeta = blockEntity.namedTag.getInt("data")
        }
        return if (dropInside) {
            arrayOf<Item>(
                    ItemFlowerPot(),
                    BlockState.of(insideID, insideMeta).getBlock(this).toItem()
            )
        } else {
            arrayOf<Item>(
                    ItemFlowerPot()
            )
        }
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    override val minX: Double
        get() = this.x + 0.3125

    @get:Override
    override val minZ: Double
        get() = this.z + 0.3125

    @get:Override
    override val maxX: Double
        get() = this.x + 0.6875

    @get:Override
    override val maxY: Double
        get() = this.y + 0.375

    @get:Override
    override val maxZ: Double
        get() = this.z + 0.6875

    @Override
    override fun canPassThrough(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemFlowerPot()
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        var PROPERTIES: BlockProperties = BlockProperties(UPDATE)
        protected fun canPlaceIntoFlowerPot(id: Int): Boolean {
            return when (id) {
                SAPLING, DEAD_BUSH, DANDELION, ROSE, RED_MUSHROOM, BROWN_MUSHROOM, CACTUS, WITHER_ROSE, WARPED_FUNGUS, CRIMSON_FUNGUS, WARPED_ROOTS, CRIMSON_ROOTS, BAMBOO -> true
                else -> false
            }
        }
    }
}