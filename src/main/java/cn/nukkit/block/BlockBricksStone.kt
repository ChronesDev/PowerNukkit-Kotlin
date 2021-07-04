package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockBricksStone @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = STONE_BRICKS

    @get:Override
    @get:Nonnull
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 30

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    fun setBrickStoneType(stoneBrickType: StoneBrickType?) {
        setPropertyValue(STONE_BRICK_TYPE, stoneBrickType)
    }

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val stoneBrickType: StoneBrickType
        get() = getPropertyValue(STONE_BRICK_TYPE)

    @get:Override
    override val name: String
        get() = stoneBrickType.getEnglishName()

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val STONE_BRICK_TYPE: BlockProperty<StoneBrickType> = ArrayBlockProperty("stone_brick_type", true, StoneBrickType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STONE_BRICK_TYPE)

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
        val NORMAL = 0

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
        val MOSSY = 1

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
        val CRACKED = 2

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getStoneBrickType()", reason = "Use the BlockProperty API instead")
        val CHISELED = 3
    }
}