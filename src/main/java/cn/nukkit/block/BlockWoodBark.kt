package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockWoodBark @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockWood(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = WOOD_BARK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = (if (isStripped) "Stripped " else "") + super.getName()

    @get:Override
    @set:Override
    override var woodType: WoodType
        get() = getPropertyValue(WoodType.PROPERTY)
        set(woodType) {
            setPropertyValue(WoodType.PROPERTY, woodType)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isStripped: Boolean
        get() = getBooleanValue(STRIPPED_BIT)
        set(stripped) {
            setBooleanValue(STRIPPED_BIT, stripped)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val STRIPPED_BIT = "stripped_bit"

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                WoodType.PROPERTY,
                BooleanBlockProperty(STRIPPED_BIT, true),
                PILLAR_AXIS
        )
    }
}