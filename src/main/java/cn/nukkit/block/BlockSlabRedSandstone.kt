package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
class BlockSlabRedSandstone @JvmOverloads constructor(meta: Int = 0) : BlockSlab(meta, DOUBLE_RED_SANDSTONE_SLAB) {
    @get:Override
    override val id: Int
        get() = RED_SANDSTONE_SLAB

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
    var slabType: StoneSlab2Type
        get() = getPropertyValue(StoneSlab2Type.PROPERTY)
        set(type) {
            setPropertyValue(StoneSlab2Type.PROPERTY, type)
        }

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id && slabType.equals(slab.getPropertyValue(StoneSlab2Type.PROPERTY))
    }

    @get:Override
    override val color: BlockColor
        get() = slabType.getColor()

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                StoneSlab2Type.PROPERTY,
                TOP_SLOT_PROPERTY
        )
        const val RED_SANDSTONE = 0
        const val PURPUR = 1
        const val PRISMARINE = 2
        const val PRISMARINE_BRICKS = 3
        const val DARK_PRISMARINE = 4
        const val MOSSY_COBBLESTONE = 5
        const val SMOOTH_SANDSTONE = 6
        const val RED_NETHER_BRICK = 7
    }
}