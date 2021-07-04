package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockSlabWood @JvmOverloads constructor(meta: Int = 0) : BlockSlab(meta, DOUBLE_WOODEN_SLAB) {
    @get:Override
    override val name: String
        get() = (if (isOnTop()) "Upper " else "") + slabName + " Wood Slab"

    @get:Override
    override val slabName: String
        get() = woodType.getEnglishName()

    @get:Override
    override val id: Int
        get() = WOOD_SLAB

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id && slab.getPropertyValue(WoodType.PROPERTY).equals(woodType)
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 20

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var woodType: WoodType
        get() = getPropertyValue(WoodType.PROPERTY)
        set(type) {
            setPropertyValue(WoodType.PROPERTY, type)
        }

    @get:Override
    override val color: BlockColor
        get() = woodType.getColor()

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                WoodType.PROPERTY,
                TOP_SLOT_PROPERTY
        )
    }
}