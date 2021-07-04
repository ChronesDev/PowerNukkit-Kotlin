package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
class BlockSlabStone @JvmOverloads constructor(meta: Int = 0) : BlockSlab(meta, DOUBLE_STONE_SLAB) {
    @get:Override
    override val id: Int
        get() = STONE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val slabName: String
        get() = slabType.getEnglishName()

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id && slabType.equals(slab.getPropertyValue(StoneSlab1Type.PROPERTY))
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

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
    override val color: BlockColor
        get() = slabType.getColor()

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                StoneSlab1Type.PROPERTY,
                TOP_SLOT_PROPERTY
        )
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