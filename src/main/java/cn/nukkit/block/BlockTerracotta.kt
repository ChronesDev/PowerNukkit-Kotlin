package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/11/24
 */
class BlockTerracotta @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(0) {
    constructor(dyeColor: TerracottaColor) : this(dyeColor.getTerracottaData()) {}

    @get:Override
    override val id: Int
        get() = TERRACOTTA

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val name: String
        get() = "Terracotta"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 1.25

    @get:Override
    override val resistance: Double
        get() = 7

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = TerracottaColor.getByTerracottaData(getDamage()).getColor()
    val dyeColor: TerracottaColor
        get() = TerracottaColor.getByTerracottaData(getDamage())
}