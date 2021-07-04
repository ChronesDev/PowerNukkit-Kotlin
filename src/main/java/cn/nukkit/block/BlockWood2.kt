package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockWood2 @JvmOverloads constructor(meta: Int = 0) : BlockWood(meta) {
    @get:Override
    override val id: Int
        get() = WOOD2

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @set:Override
    override var woodType: WoodType
        get() = getPropertyValue(NEW_LOG_TYPE)
        set(woodType) {
            setPropertyValue(NEW_LOG_TYPE, woodType)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NEW_LOG_TYPE: BlockProperty<WoodType> = ArrayBlockProperty("new_log_type", true, arrayOf<WoodType>(
                WoodType.ACACIA, WoodType.DARK_OAK
        ), 2)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(NEW_LOG_TYPE, PILLAR_AXIS)
        const val ACACIA = 0
        const val DARK_OAK = 1
    }
}