package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockPlanks @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = WOODEN_PLANKS

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 20

    @get:Override
    override val name: String
        get() = woodType.getEnglishName().toString() + " Wood Planks"

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
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = woodType.getColor()

    companion object {
        const val OAK = 0
        const val SPRUCE = 1
        const val BIRCH = 2
        const val JUNGLE = 3
        const val ACACIA = 4
        const val DARK_OAK = 5

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(WoodType.PROPERTY)
    }
}