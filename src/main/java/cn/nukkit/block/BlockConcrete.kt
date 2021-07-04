package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
class BlockConcrete @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = CONCRETE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 9

    @get:Override
    override val hardness: Double
        get() = 1.8

    @get:Override
    override val name: String
        get() = "Concrete"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

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