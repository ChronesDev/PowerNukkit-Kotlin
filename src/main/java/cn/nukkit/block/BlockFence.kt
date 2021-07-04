package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author xtypr
 * @since 2015/12/7
 */
@PowerNukkitDifference(info = "Implements BlockConnectable only on PowerNukkit", since = "1.3.0.0-PN")
class BlockFence @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), BlockConnectable {
    @get:Override
    override val id: Int
        get() = FENCE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var woodType: Optional<WoodType>
        get() = Optional.of(getPropertyValue(WoodType.PROPERTY))
        set(woodType) {
            setPropertyValue(WoodType.PROPERTY, woodType)
        }

    @get:Override
    override val name: String
        get() = getPropertyValue(WoodType.PROPERTY).getEnglishName().toString() + " Fence"

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val north = canConnect(this.north())
        val south = canConnect(this.south())
        val west = canConnect(this.west())
        val east = canConnect(this.east())
        val n: Double = if (north) 0 else 0.375
        val s: Double = if (south) 1 else 0.625
        val w: Double = if (west) 0 else 0.375
        val e: Double = if (east) 1 else 0.625
        return SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        )
    }

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 20

    @Override
    fun canConnect(block: Block): Boolean {
        if (block is BlockFence) {
            return if (block.getId() === NETHER_BRICK_FENCE || id == NETHER_BRICK_FENCE) {
                block.getId() === id
            } else true
        }
        if (block is BlockTrapdoor) {
            val trapdoor: BlockTrapdoor = block
            return trapdoor.isOpen() && trapdoor.getBlockFace() === calculateFace(this, trapdoor)
        }
        return block is BlockFenceGate || block.isSolid() && !block.isTransparent()
    }

    @get:Override
    override val color: BlockColor
        get() = getPropertyValue(WoodType.PROPERTY).getColor()

    companion object {
        val PROPERTIES: BlockProperties = BlockProperties(WoodType.PROPERTY)

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_OAK = 0

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_SPRUCE = 1

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_BIRCH = 2

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_JUNGLE = 3

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_ACACIA = 4

        @Deprecated
        @DeprecationDetails(reason = "Moved to the block property system", since = "1.4.0.0-PN", replaceWith = "getWoodType()")
        val FENCE_DARK_OAK = 5
    }
}