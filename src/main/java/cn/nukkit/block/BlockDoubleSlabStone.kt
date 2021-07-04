package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase only in PowerNukkit")
class BlockDoubleSlabStone @JvmOverloads constructor(meta: Int = 0) : BlockDoubleSlabBase(meta) {
    @get:Override
    override val id: Int
        get() = DOUBLE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlabStone.PROPERTIES

    @get:Override
    override val resistance: Double
        get() = if (toolType > ItemTool.TIER_WOODEN) 30 else 15

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var slabType: StoneSlab1Type
        get() = getPropertyValue(StoneSlab1Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab1Type.PROPERTY, type)
        }

    @get:Override
    override val singleSlabId: Int
        get() = STONE_SLAB

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

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
        const val STONE = 0
        const val SANDSTONE = 1
        const val WOODEN = 2
        const val COBBLESTONE = 3
        const val BRICK = 4
        const val STONE_BRICK = 5
        const val QUARTZ = 6
        const val NETHER_BRICK = 7
    }
}