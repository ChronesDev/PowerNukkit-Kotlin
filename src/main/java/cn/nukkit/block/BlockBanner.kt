package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author PetteriM1
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
class BlockBanner @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntityBanner?> {
    @get:Override
    override val id: Int
        get() = STANDING_BANNER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSignPost.PROPERTIES

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BANNER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBanner?>
        get() = BlockEntityBanner::class.java

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val name: String
        get() = "Banner"

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB? {
        return null
    }

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (face === BlockFace.DOWN) {
            return false
        }
        val layer0: Block = level.getBlock(this, 0)
        val layer1: Block = level.getBlock(this, 1)
        if (face === BlockFace.UP) {
            var direction: CompassRoseDirection = GROUND_SIGN_DIRECTION.getValueForMeta(
                    Math.floor(((if (player != null) player.yaw else 0) + 180) * 16 / 360 + 0.5) as Int and 0x0f
            )
            direction = direction
            if (!this.getLevel().setBlock(block, this, true)) {
                return false
            }
        } else {
            val wall = Block.get(BlockID.WALL_BANNER) as BlockBanner
            wall.blockFace = face
            if (!this.getLevel().setBlock(block, wall, true)) {
                return false
            }
        }
        val nbt: CompoundTag = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                .putInt("Base", item.getDamage() and 0xf)
        val type: Tag = item.getNamedTagEntry("Type")
        if (type is IntTag) {
            nbt.put("Type", type)
        }
        val patterns: Tag = item.getNamedTagEntry("Patterns")
        if (patterns is ListTag) {
            nbt.put("Patterns", patterns)
        }
        return try {
            createBlockEntity(nbt)
            true
        } catch (e: Exception) {
            log.error("Failed to create the block entity {} at {}", blockEntityType, getLocation(), e)
            level.setBlock(layer0, 0, layer0, true)
            level.setBlock(layer0, 1, layer1, true)
            false
        }
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() === BlockID.AIR) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun toItem(): Item {
        val banner: BlockEntityBanner = getBlockEntity()
        val item: Item = Item.get(ItemID.BANNER)
        if (banner != null) {
            item.setDamage(banner.getBaseColor() and 0xf)
            val type: Int = banner.namedTag.getInt("Type")
            if (type > 0) {
                item.setNamedTag((if (item.hasCompoundTag()) item.getNamedTag() else CompoundTag())
                        .putInt("Type", type))
            }
            val patterns: ListTag<CompoundTag> = banner.namedTag.getList("Patterns", CompoundTag::class.java)
            if (patterns.size() > 0) {
                item.setNamedTag((if (item.hasCompoundTag()) item.getNamedTag() else CompoundTag())
                        .putList(patterns))
            }
        }
        return item
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var direction: CompassRoseDirection
        get() = getPropertyValue(GROUND_SIGN_DIRECTION)
        set(direction) {
            setPropertyValue(GROUND_SIGN_DIRECTION, direction)
        }

    @get:Override
    @get:PowerNukkitDifference(info = "Was returning the wrong face, it now return the closest face, or the left face if even", since = "1.4.0.0-PN")
    @set:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = direction.getClosestBlockFace()
        set(face) {
            direction = face.getCompassRoseDirection()
        }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()
    val dyeColor: DyeColor
        get() {
            if (this.level != null) {
                val blockEntity: BlockEntityBanner = getBlockEntity()
                if (blockEntity != null) {
                    return blockEntity.getDyeColor()
                }
            }
            return DyeColor.WHITE
        }
}