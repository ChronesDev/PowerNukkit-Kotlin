package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

class BlockSlabStone3 @JvmOverloads constructor(meta: Int = 0) : BlockSlab(meta, DOUBLE_STONE_SLAB3) {
    @get:Override
    override val id: Int
        get() = STONE_SLAB3

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var slabType: StoneSlab3Type
        get() = getPropertyValue(StoneSlab3Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab3Type.PROPERTY, type)
        }

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id && slabType.equals(slab.getPropertyValue(StoneSlab3Type.PROPERTY))
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
                StoneSlab3Type.PROPERTY,
                TOP_SLOT_PROPERTY
        )
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