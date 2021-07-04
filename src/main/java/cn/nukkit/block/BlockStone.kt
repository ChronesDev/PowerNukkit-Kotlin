package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockStone @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = STONE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var stoneType: StoneType
        get() = getPropertyValue(STONE_TYPE)
        set(stoneType) {
            setPropertyValue(STONE_TYPE, stoneType)
        }

    @get:Override
    override val name: String
        get() = stoneType.getEnglishName()

    @get:Override
    override val color: BlockColor
        get() = stoneType.getColor()

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= toolTier) {
            arrayOf<Item>(
                    if (StoneType.STONE.equals(stoneType)) Item.getBlock(BlockID.COBBLESTONE) else toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val STONE_TYPE: BlockProperty<StoneType> = ArrayBlockProperty("stone_type", true, StoneType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STONE_TYPE)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val NORMAL = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val GRANITE = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val POLISHED_GRANITE = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val DIORITE = 3

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val POLISHED_DIORITE = 4

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val ANDESITE = 5

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "getStoneType()", reason = "Use the BlockProperty API instead")
        val POLISHED_ANDESITE = 6
    }
}