package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockSandstone @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = SANDSTONE

    @get:Override
    @get:Nonnull
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    override val properties: BlockProperties
        get() = PROPERTIES

    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var sandstoneType: SandStoneType
        get() = getPropertyValue(SAND_STONE_TYPE)
        set(sandStoneType) {
            setPropertyValue(SAND_STONE_TYPE, sandStoneType)
        }

    @get:Override
    override val hardness: Double
        get() = if (SandStoneType.SMOOTH.equals(sandstoneType)) 2 else 0.8

    @get:Override
    override val resistance: Double
        get() = if (SandStoneType.SMOOTH.equals(sandstoneType)) 6 else 0.8

    @get:Override
    override val name: String
        get() = sandstoneType.getEnglishName()

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val SAND_STONE_TYPE: BlockProperty<SandStoneType> = ArrayBlockProperty("sand_stone_type", true, SandStoneType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SAND_STONE_TYPE)

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
        val NORMAL = 0

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
        val CHISELED = 1

        @Deprecated
        @DeprecationDetails(since = "1.5.0.0-PN", replaceWith = "getSandstoneBlockType()", reason = "Use the BlockProperty API instead")
        val SMOOTH = 2
    }
}