package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
abstract class BlockWoodStripped @PowerNukkitOnly constructor(meta: Int) : BlockWood(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    abstract override val id: Int

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PILLAR_PROPERTIES

    @get:Override
    protected override val strippedState: BlockState
        protected get() = getCurrentState()

    @get:Override
    override val name: String
        get() = "Stripped " + getWoodType().toString() + " Log"

    @set:Override
    override var woodType: WoodType
        get() = super.woodType
        set(woodType) {
            if (!woodType.equals(getWoodType())) {
                throw InvalidBlockPropertyValueException(WoodType.PROPERTY, getWoodType(), woodType,
                        "Only the current value is supported for this block")
            }
        }

    @Override
    override fun canBeActivated(): Boolean {
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }
}