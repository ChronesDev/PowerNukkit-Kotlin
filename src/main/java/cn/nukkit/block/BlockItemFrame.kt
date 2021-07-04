package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder and Faceable only in PowerNukkit")
class BlockItemFrame @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), BlockEntityHolder<BlockEntityItemFrame?>, Faceable {
    @get:Override
    override val id: Int
        get() = ITEM_FRAME_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(FACING_DIRECTION)
        set(face) {
            setPropertyValue(FACING_DIRECTION, face)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isStoringMap: Boolean
        get() = getBooleanValue(HAS_MAP)
        set(map) {
            setBooleanValue(HAS_MAP, map)
        }

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.ITEM_FRAME

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityItemFrame?>
        get() = BlockEntityItemFrame::class.java

    @get:Override
    override val name: String
        get() = "Item Frame"

    @PowerNukkitDifference(info = "Allow to stay in walls", since = "1.3.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val support: Block = this.getSideAtLayer(0, facing.getOpposite())
            if (!support.isSolid() && support.getId() !== COBBLE_WALL) {
                this.level.useBreakOn(this)
                return type
            }
        }
        return 0
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onTouch(@Nullable player: Player?, action: Action): Int {
        if (action === Action.LEFT_CLICK_BLOCK && (player == null || !player.isCreative() && !player.isSpectator())) {
            getOrCreateBlockEntity().dropItem(player)
        }
        return super.onTouch(player, action)
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        val itemFrame: BlockEntityItemFrame = getOrCreateBlockEntity()
        if (itemFrame.getItem().isNull()) {
            val itemOnFrame: Item = item.clone()
            if (player != null && !player.isCreative()) {
                itemOnFrame.setCount(itemOnFrame.getCount() - 1)
                player.getInventory().setItemInHand(itemOnFrame)
            }
            itemOnFrame.setCount(1)
            itemFrame.setItem(itemOnFrame)
            if (itemOnFrame.getId() === ItemID.MAP) {
                isStoringMap = true
                this.getLevel().setBlock(this, this, true)
            }
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ADDED)
        } else {
            itemFrame.setItemRotation((itemFrame.getItemRotation() + 1) % 8)
            if (isStoringMap) {
                isStoringMap = false
                this.getLevel().setBlock(this, this, true)
            }
            this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_ITEM_ROTATED)
        }
        return true
    }

    @PowerNukkitDifference(info = "Allow to place on walls", since = "1.3.0.0-PN")
    @Override
    fun place(@Nonnull item: Item, @Nonnull block: Block, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (target.getId() !== COBBLE_WALL && (!target.isSolid() || block.isSolid() && !block.canBeReplaced())) {
            return false
        }
        blockFace = face
        isStoringMap = item.getId() === ItemID.MAP
        val nbt: CompoundTag = CompoundTag()
                .putByte("ItemRotation", 0)
                .putFloat("ItemDropChance", 1.0f)
        if (item.hasCustomBlockData()) {
            for (aTag in item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag)
            }
        }
        val frame: BlockEntityItemFrame = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt)
                ?: return false
        this.getLevel().addSound(this, Sound.BLOCK_ITEMFRAME_PLACE)
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        this.getLevel().setBlock(this, layer, Block.get(BlockID.AIR), true, true)
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ITEM_FRAME_REMOVED)
        return true
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        val itemFrame: BlockEntityItemFrame = getBlockEntity()
        return if (itemFrame != null && ThreadLocalRandom.current().nextFloat() <= itemFrame.getItemDropChance()) {
            arrayOf<Item>(
                    toItem(), itemFrame.getItem().clone()
            )
        } else {
            arrayOf<Item>(
                    toItem()
            )
        }
    }

    @Override
    override fun toItem(): Item {
        return ItemItemFrame()
    }

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val blockEntity: BlockEntityItemFrame = getBlockEntity()
            return if (blockEntity != null) {
                blockEntity.getAnalogOutput()
            } else super.getComparatorInputOverride()
        }
    val facing: BlockFace
        get() = blockFace

    @get:Override
    override val hardness: Double
        get() = 0.25

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @PowerNukkitOnly("Will calculate the correct AABB")
    @Since("1.3.0.0-PN")
    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val aabb = arrayOf(doubleArrayOf(2.0 / 16, 14.0 / 16), doubleArrayOf(2.0 / 16, 14.0 / 16), doubleArrayOf(2.0 / 16, 14.0 / 16))
        val facing: BlockFace = facing
        if (facing.getAxisDirection() === POSITIVE) {
            val axis: Int = facing.getAxis().ordinal()
            aabb[axis][0] = 0
            aabb[axis][1] = 1.0 / 16
        }
        return SimpleAxisAlignedBB(
                aabb[0][0] + x, aabb[1][0] + y, aabb[2][0] + z,
                aabb[0][1] + x, aabb[1][1] + y, aabb[2][1] + z
        )
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val HAS_MAP: BooleanBlockProperty = BooleanBlockProperty("item_frame_map_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, HAS_MAP)
    }
}