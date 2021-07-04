package cn.nukkit.block

/**
 * @author Justin
 */
import cn.nukkit.Player

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
class BlockSkull @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), RedstoneComponent, BlockEntityHolder<BlockEntitySkull?> {
    @get:Override
    override val id: Int
        get() = SKULL_BLOCK

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
        get() = BlockEntity.SKULL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntitySkull?>
        get() = BlockEntitySkull::class.java

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
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @get:Override
    override val name: String
        get() {
            var itemMeta = 0
            if (this.level != null) {
                val blockEntity: BlockEntitySkull = getBlockEntity()
                if (blockEntity != null) {
                    itemMeta = blockEntity.namedTag.getByte("SkullType")
                }
            }
            return ItemSkull.getItemSkullName(itemMeta)
        }

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player): Boolean {
        when (face) {
            NORTH, SOUTH, EAST, WEST, UP -> this.setDamage(face.getIndex())
            DOWN -> return false
            else -> return false
        }
        val nbt: CompoundTag = CompoundTag()
                .putByte("SkullType", item.getDamage())
                .putByte("Rot", Math.floor(player.yaw * 16 / 360 + 0.5) as Int and 0x0f)
        if (item.hasCustomBlockData()) {
            for (aTag in item.getCustomBlockData().getAllTags()) {
                nbt.put(aTag.getName(), aTag)
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
        // TODO: 2016/2/3 SPAWN WITHER
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        if (type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_NORMAL || !level.getServer().isRedstoneEnabled()) {
            return 0
        }
        val entity: BlockEntitySkull = getBlockEntity()
        if (entity == null || entity.namedTag.getByte("SkullType") !== 5) {
            return 0
        }
        val ev = RedstoneUpdateEvent(this)
        getLevel().getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return 0
        }
        entity.setMouthMoving(this.isGettingPower())
        return Level.BLOCK_UPDATE_REDSTONE
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        val entitySkull: BlockEntitySkull = getBlockEntity()
        var dropMeta = 0
        if (entitySkull != null) {
            dropMeta = entitySkull.namedTag.getByte("SkullType")
        }
        return arrayOf<Item>(
                ItemSkull(dropMeta)
        )
    }

    @Override
    override fun toItem(): Item {
        val blockEntity: BlockEntitySkull = getBlockEntity()
        var itemMeta = 0
        if (blockEntity != null) {
            itemMeta = blockEntity.namedTag.getByte("SkullType")
        }
        return ItemSkull(itemMeta)
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val NO_DROP: BooleanBlockProperty = BooleanBlockProperty("no_drop_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION, NO_DROP)
    }
}