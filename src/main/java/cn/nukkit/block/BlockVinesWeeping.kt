package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * Properties and behaviour definitions of the [BlockID.WEEPING_VINES] block.
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockVinesWeeping : BlockVinesNether {
    /**
     * Creates a `weeping_vine` with age `0`.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    /**
     * Creates a `weeping_vine` from a meta compatible with [.getProperties].
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
        get() = WEEPING_VINES

    @get:Override
    override val name: String
        get() = "Weeping Vines"

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @get:Nonnull
    override val growthDirection: BlockFace
        get() = BlockFace.DOWN

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var vineAge: Int
        get() = getIntValue(WEEPING_VINES_AGE)
        set(vineAge) {
            setIntValue(WEEPING_VINES_AGE, vineAge)
        }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val maxVineAge: Int
        get() = WEEPING_VINES_AGE.getMaxValue()

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    companion object {
        /**
         * Increments for every block the weeping vine grows.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WEEPING_VINES_AGE: IntBlockProperty = IntBlockProperty(
                "weeping_vines_age", false, 25)

        /**
         * Holds the `weeping_vines` block property definitions.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(WEEPING_VINES_AGE)
    }
}