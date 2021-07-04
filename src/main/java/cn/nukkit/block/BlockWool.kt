package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockWool @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    constructor(dyeColor: DyeColor) : this(dyeColor.getWoolData()) {}

    @get:Override
    override val name: String
        get() = dyeColor.getName().toString() + " Wool"

    @get:Override
    override val id: Int
        get() = WOOL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHEARS

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4

    @get:Override
    override val burnChance: Int
        get() = 30

    @get:Override
    override val burnAbility: Int
        get() = 60

    @get:Override
    override val color: BlockColor
        get() = DyeColor.getByWoolData(getDamage()).getColor()
    val dyeColor: DyeColor
        get() = DyeColor.getByWoolData(getDamage())

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.COLOR_BLOCK_PROPERTIES
    }
}