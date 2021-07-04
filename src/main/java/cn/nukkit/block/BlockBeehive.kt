package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBeehive @PowerNukkitOnly protected constructor(meta: Int) : BlockSolidMeta(meta), Faceable, BlockEntityHolder<BlockEntityBeehive?> {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BEEHIVE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Beehive"

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BEEHIVE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBeehive?>
        get() = BlockEntityBeehive::class.java

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 20

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        blockFace = if (player == null) {
            BlockFace.SOUTH
        } else {
            player.getDirection().getOpposite()
        }
        var honeyLevel = if (item.hasCustomBlockData()) item.getCustomBlockData().getByte("HoneyLevel") else 0
        honeyLevel = honeyLevel
        val beehive: BlockEntityBeehive = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, item.getCustomBlockData())
                ?: return false
        if (beehive.namedTag.getByte("ShouldSpawnBees") > 0) {
            val validSpawnFaces: List<BlockFace> = beehive.scanValidSpawnFaces(true)
            for (occupant in beehive.getOccupants()) {
                beehive.spawnOccupant(occupant, validSpawnFaces)
            }
            beehive.namedTag.putByte("ShouldSpawnBees", 0)
        }
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player): Boolean {
        if (item.getId() === ItemID.SHEARS && isFull) {
            honeyCollected(player)
            level.addSound(add(0.5, 0.5, 0.5), Sound.BLOCK_BEEHIVE_SHEAR)
            item.useOn(this)
            for (i in 0..2) {
                level.dropItem(this, Item.get(ItemID.HONEYCOMB))
            }
            return true
        }
        return false
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @PowerNukkitOnly
    fun honeyCollected(player: Player) {
        honeyCollected(player, level.getServer().getDifficulty() > 0 && !player.isCreative())
    }

    @PowerNukkitOnly
    fun honeyCollected(player: Player?, angerBees: Boolean) {
        honeyLevel = 0
        if (down().getId() !== CAMPFIRE_BLOCK && angerBees) {
            angerBees(player)
        }
    }

    @PowerNukkitOnly
    fun angerBees(player: Player?) {
        val beehive: BlockEntityBeehive = getBlockEntity()
        if (beehive != null) {
            beehive.angerBees(player)
        }
    }

    @Override
    override fun toItem(): Item {
        val item: Item = Item.get(getItemId(), 0, 1)
        if (level != null) {
            val beehive: BlockEntityBeehive = getBlockEntity()
            if (beehive != null) {
                beehive.saveNBT()
                if (!beehive.isHoneyEmpty() || !beehive.isEmpty()) {
                    val copy: CompoundTag = beehive.namedTag.copy()
                    copy.putByte("HoneyLevel", honeyLevel)
                    item.setCustomBlockData(copy)
                }
            }
        }
        return item
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    override fun mustSilkTouch(vector: Vector3?, layer: Int, face: BlockFace?, item: Item?, player: Player?): Boolean {
        if (player != null) {
            val beehive: BlockEntityBeehive = getBlockEntity()
            if (beehive != null && !beehive.isEmpty()) {
                return true
            }
        }
        return super.mustSilkTouch(vector, layer, face, item, player)
    }

    @Since("1.2.1.0-PN")
    @PowerNukkitOnly
    @Override
    override fun mustDrop(vector: Vector3?, layer: Int, face: BlockFace?, item: Item?, player: Player?): Boolean {
        return mustSilkTouch(vector, layer, face, item, player) || super.mustDrop(vector, layer, face, item, player)
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(Item.getBlock(BlockID.BEEHIVE))
    }

    @get:Override
    @set:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var honeyLevel: Int
        get() = getPropertyValue(HONEY_LEVEL)
        set(honeyLevel) {
            setPropertyValue(HONEY_LEVEL, honeyLevel)
        }

    @get:PowerNukkitOnly
    val isEmpty: Boolean
        get() = honeyLevel == HONEY_LEVEL.getMinValue()

    @get:PowerNukkitOnly
    val isFull: Boolean
        get() = getPropertyValue(HONEY_LEVEL) === cn.nukkit.block.BlockBeehive.Companion.HONEY_LEVEL.getMaxValue()

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() = honeyLevel

    companion object {
        @PowerNukkitOnly
        val HONEY_LEVEL: IntBlockProperty = IntBlockProperty("honey_level", false, 5)

        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, HONEY_LEVEL)
    }
}