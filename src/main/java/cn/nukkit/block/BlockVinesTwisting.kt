package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * Properties and behaviour definitions of the [BlockID.TWISTING_VINES] block.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockVinesTwisting : BlockVinesNether {
    /**
     * Creates a `twisting_vine` with age `0`.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        // Does nothing
    }

    /**
     * Creates a `twisting_vine` from a meta compatible with [.getProperties].
     * @throws InvalidBlockPropertyMetaException If the meta is incompatible
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val id: Int
        get() = TWISTING_VINES

    @get:Override
    override val name: String
        get() = "Twisting Vines"

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @get:Nonnull
    override val growthDirection: BlockFace
        get() = BlockFace.UP

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Throws(InvalidBlockPropertyValueException::class)
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var vineAge: Int
        get() = getIntValue(TWISTING_VINES_AGE)
        set(vineAge) {
            setIntValue(TWISTING_VINES_AGE, vineAge)
        }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val maxVineAge: Int
        get() = TWISTING_VINES_AGE.getMaxValue()

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR

    companion object {
        /**
         * Increments for every block the twisting vine grows.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TWISTING_VINES_AGE: IntBlockProperty = IntBlockProperty(
                "twisting_vines_age", false, 25)

        /**
         * Holds the `twisting_vines` block property definitions.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(TWISTING_VINES_AGE)
    }
}