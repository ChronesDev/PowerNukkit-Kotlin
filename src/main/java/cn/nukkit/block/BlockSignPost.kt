package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Nukkit Project Team
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
class BlockSignPost @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntitySign?> {
    @get:Override
    override val id: Int
        get() = SIGN_POST

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntitySign?>
        get() = BlockEntitySign::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.SIGN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    override val name: String
        get() = "Sign Post"

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1
    protected val postId: Int
        protected get() = id
    val wallId: Int
        get() = WALL_SIGN

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (face === BlockFace.DOWN) {
            return false
        }
        val layer0: Block = level.getBlock(this, 0)
        val layer1: Block = level.getBlock(this, 1)
        val nbt: CompoundTag = CompoundTag()
                .putString("Text1", "")
                .putString("Text2", "")
                .putString("Text3", "")
                .putString("Text4", "")
        if (face === BlockFace.UP) {
            val direction: CompassRoseDirection = GROUND_SIGN_DIRECTION.getValueForMeta(
                    Math.floor(((if (player != null) player.yaw else 0) + 180) * 16 / 360 + 0.5) as Int and 0x0f
            )
            val post: BlockState = BlockState.of(postId).withProperty(GROUND_SIGN_DIRECTION, direction)
            getLevel().setBlock(block, post.getBlock(block), true)
        } else {
            val wall: BlockState = BlockState.of(wallId).withProperty(FACING_DIRECTION, face)
            getLevel().setBlock(block, wall.getBlock(block), true)
        }
        if (player != null) {
            nbt.putString("Creator", player.getUniqueId().toString())
        }
        if (item.hasCustomBlockData()) {
            for (aTag in item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag)
            }
        }
        return try {
            createBlockEntity(nbt)
            true
        } catch (e: Exception) {
            log.warn("Failed to create block entity {} at {}", blockEntityType, getLocation(), e)
            level.setBlock(layer0, 0, layer0, true)
            level.setBlock(layer1, 0, layer1, true)
            false
        }
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (down().getId() === Block.AIR) {
                getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun toItem(): Item {
        return ItemSign()
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var signDirection: CompassRoseDirection
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
        get() = signDirection.getClosestBlockFace()
        set(face) {
            signDirection = face.getCompassRoseDirection()
        }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val GROUND_SIGN_DIRECTION: BlockProperty<CompassRoseDirection> = ArrayBlockProperty("ground_sign_direction", false, arrayOf<CompassRoseDirection>(
                SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST,
                WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST,
                NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST,
                EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST
        )).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(GROUND_SIGN_DIRECTION)
    }
}