package cn.nukkit.blockstate

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@Value
class BlockStateRepair {
    /**
     * The block ID of the block state that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var blockId = 0

    /**
     * The block properties of the block stat that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    var properties: BlockProperties? = null

    /**
     * The state that was originally received when the repair started.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    var originalState: Number? = null

    /**
     * The current state that is being repaired.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    var currentState: Number? = null

    /**
     * The state after the repair. It does not consider [.getProposedPropertyValue].
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    var nextState: Number? = null

    /**
     * How many repairs was applied to the original state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var repairs = 0

    /**
     * The property that reported the invalid state, `null` if all the properties
     * was validated but the state have more bits to validate.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    var property: BlockProperty<*>? = null

    /**
     * The bit position of the invalid property value, when [.getProperty] is `null` this indicates
     * the start index of the [.getBrokenPropertyMeta].
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var propertyOffset = 0

    /**
     * The current invalid int value that is in the property bit space.
     * If the [.getProperty] is `null` than it will hold all remaining data that can be stored in an integer
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var brokenPropertyMeta = 0

    /**
     * The property value that can be set to fix the current block state. It's usually the default property value.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    var fixedPropertyValue: Serializable? = null

    /**
     * The proposed property int value to fix the current block state,
     * if the proposed value is not valid [.getFixedPropertyValue] will be used.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @NonFinal
    @Nonnull
    var proposedPropertyValue: Serializable? = null

    /**
     * The exception that was thrown when trying to validate the [.getCurrentState] and resulted in this repair.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    var validationException: InvalidBlockPropertyException? = null
}