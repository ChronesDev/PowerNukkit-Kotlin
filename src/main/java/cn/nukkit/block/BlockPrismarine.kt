package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

class BlockPrismarine @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = PRISMARINE

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

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = prismarineBlockType.getEnglishName()

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var prismarineBlockType: PrismarineBlockType
        get() = getPropertyValue(PRISMARINE_BLOCK_TYPE)
        set(prismarineBlockType) {
            setPropertyValue(PRISMARINE_BLOCK_TYPE, prismarineBlockType)
        }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = prismarineBlockType.getColor()

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PRISMARINE_BLOCK_TYPE: BlockProperty<PrismarineBlockType> = ArrayBlockProperty("prismarine_block_type", true, PrismarineBlockType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(PRISMARINE_BLOCK_TYPE)

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
        val NORMAL = 0

        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
        val DARK = 1

        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getPrismarineBlockType()", reason = "Use the BlockProperty API instead")
        val BRICKS = 2
    }
}