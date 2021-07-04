package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Extends BlockLog instead of BlockSolidMeta only in PowerNukkit", since = "1.4.0.0-PN")
class BlockWood @JvmOverloads constructor(meta: Int = 0) : BlockLog(meta) {
    @get:Override
    override val id: Int
        get() = WOOD

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
        get() = 2

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var woodType: WoodType
        get() = getPropertyValue(OLD_LOG_TYPE)
        set(woodType) {
            setPropertyValue(OLD_LOG_TYPE, woodType)
        }

    @get:Override
    override val name: String
        get() = woodType.getEnglishName().toString() + " Wood"

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 10

    @get:Override
    protected override val strippedState: BlockState
        protected get() {
            val strippedId: Int
            when (woodType) {
                OAK -> strippedId = STRIPPED_OAK_LOG
                SPRUCE -> strippedId = STRIPPED_SPRUCE_LOG
                BIRCH -> strippedId = STRIPPED_BIRCH_LOG
                JUNGLE -> strippedId = STRIPPED_JUNGLE_LOG
                ACACIA -> strippedId = STRIPPED_ACACIA_LOG
                DARK_OAK -> strippedId = STRIPPED_DARK_OAK_LOG
                else -> strippedId = STRIPPED_OAK_LOG
            }
            return BlockState.of(strippedId).withProperty(PILLAR_AXIS, getPillarAxis())
        }

    @get:Override
    override val color: BlockColor
        get() = woodType.getColor()

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OLD_LOG_TYPE: BlockProperty<WoodType> = ArrayBlockProperty("old_log_type", true, arrayOf<WoodType>(
                WoodType.OAK, WoodType.SPRUCE, WoodType.BIRCH, WoodType.JUNGLE
        ))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(OLD_LOG_TYPE, PILLAR_AXIS)
        const val OAK = 0
        const val SPRUCE = 1
        const val BIRCH = 2
        const val JUNGLE = 3
    }
}