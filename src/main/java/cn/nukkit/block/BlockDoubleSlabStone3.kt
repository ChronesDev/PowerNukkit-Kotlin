package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase instead of BlockDoubleSlabStone only in PowerNukkit")
class BlockDoubleSlabStone3 @JvmOverloads constructor(meta: Int = 0) : BlockDoubleSlabBase(meta) {
    @get:Override
    override val id: Int
        get() = DOUBLE_STONE_SLAB3

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlabStone3.PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var slabType: StoneSlab3Type
        get() = getPropertyValue(StoneSlab3Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab3Type.PROPERTY, type)
        }

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

    @get:Override
    override val resistance: Double
        get() = if (toolType > ItemTool.TIER_WOODEN) 30 else 15

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val singleSlabId: Int
        get() = STONE_SLAB3

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = slabType.getColor()

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        const val END_STONE_BRICKS = 0
        const val SMOOTH_RED_SANDSTONE = 1
        const val POLISHED_ANDESITE = 2
        const val ANDESITE = 3
        const val DIORITE = 4
        const val POLISHED_DIORITE = 5
        const val GRANITE = 6
        const val POLISHED_GRANITE = 7
    }
}