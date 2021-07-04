package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase only in PowerNukkit")
class BlockDoubleSlabRedSandstone @JvmOverloads constructor(meta: Int = 0) : BlockDoubleSlabBase(meta) {
    @get:Override
    override val id: Int
        get() = DOUBLE_RED_SANDSTONE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlabRedSandstone.PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var slabType: StoneSlab2Type
        get() = getPropertyValue(StoneSlab2Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab2Type.PROPERTY, type)
        }

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val singleSlabId: Int
        get() = RED_SANDSTONE_SLAB

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = slabType.getColor()
}