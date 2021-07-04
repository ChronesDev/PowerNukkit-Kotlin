package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

class BlockSlabStone4 @JvmOverloads constructor(meta: Int = 0) : BlockSlab(meta, DOUBLE_STONE_SLAB4) {
    @get:Override
    override val id: Int
        get() = STONE_SLAB4

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var slabType: StoneSlab4Type
        get() = getPropertyValue(StoneSlab4Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab4Type.PROPERTY, type)
        }

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id && slabType.equals(slab.getPropertyValue(StoneSlab4Type.PROPERTY))
    }

    @get:Override
    override val color: BlockColor
        get() = slabType.getColor()

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                StoneSlab4Type.PROPERTY,
                TOP_SLOT_PROPERTY
        )
        const val MOSSY_STONE_BRICKS = 0
        const val SMOOTH_QUARTZ = 1
        const val STONE = 2
        const val CUT_SANDSTONE = 3
        const val CUT_RED_SANDSTONE = 4
    }
}