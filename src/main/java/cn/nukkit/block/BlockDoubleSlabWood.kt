package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author xtypr
 * @since 2015/12/2
 */
@PowerNukkitDifference(info = "Extends BlockDoubleSlabBase only in PowerNukkit")
class BlockDoubleSlabWood @JvmOverloads constructor(meta: Int = 0) : BlockDoubleSlabBase(meta) {
    @get:Override
    override val id: Int
        get() = DOUBLE_WOOD_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlabWood.PROPERTIES

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
    override val slabName: String
        get() = woodType.getEnglishName()

    @get:Override
    override val name: String
        get() = "Double $slabName Wood Slab"

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val singleSlabId: Int
        get() = WOOD_SLAB

    //TODO Adjust or remove this when merging https://github.com/PowerNukkit/PowerNukkit/pull/370
    @Override
    protected override fun isCorrectTool(item: Item?): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = woodType.getColor()
}