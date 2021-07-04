package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockCoralFanHang2 @PowerNukkitOnly constructor(meta: Int) : BlockCoralFanHang(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_FAN_HANG2

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val type: Int
        get() = if (getDamage() and 1 === 0) {
            BlockCoral.TYPE_BUBBLE
        } else {
            BlockCoral.TYPE_FIRE
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HANG2_TYPE: ArrayBlockProperty<CoralType> = ArrayBlockProperty("coral_hang_type_bit", true, arrayOf<CoralType>(CoralType.PURPLE, CoralType.RED)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HANG2_TYPE, PERMANENTLY_DEAD, HANG_DIRECTION)
    }
}