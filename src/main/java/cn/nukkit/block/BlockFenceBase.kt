package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockFenceBase @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockFence(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    @get:DeprecationDetails(reason = "Will always returns empty on this type. It is here for backward compatibility", since = "1.4.0.0-PN")
    @get:Deprecated
    @set:Override
    @set:DeprecationDetails(reason = "Only accepts null. It is here for backward compatibility", since = "1.4.0.0-PN")
    @set:Deprecated
    override var woodType: Optional<WoodType>
        get() = Optional.empty()
        set(woodType) {
            if (woodType != null) {
                throw InvalidBlockPropertyValueException(WoodType.PROPERTY, null, woodType, "This block don't have a regular wood type")
            }
        }
}