package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockCoralFanHang3 @PowerNukkitOnly constructor(meta: Int) : BlockCoralFanHang(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_FAN_HANG3

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val type: Int
        get() = BlockCoral.TYPE_HORN

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HANG3_TYPE: ArrayBlockProperty<CoralType> = ArrayBlockProperty("coral_hang_type_bit", true, arrayOf<CoralType>(CoralType.YELLOW)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HANG3_TYPE, PERMANENTLY_DEAD, HANG_DIRECTION)
    }
}