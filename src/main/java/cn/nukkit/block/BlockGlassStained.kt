package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
class BlockGlassStained : BlockGlass {
    constructor() {
        // Does nothing
    }

    constructor(meta: Int) {
        if (meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true)
        }
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val id: Int
        get() = STAINED_GLASS

    @get:Override
    override val name: String
        get() = dyeColor.getName().toString() + " Stained Glass"

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()

    @get:Nonnull
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var dyeColor: DyeColor
        get() = getPropertyValue(COLOR)
        set(color) {
            setPropertyValue(COLOR, color)
        }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.COLOR_BLOCK_PROPERTIES
    }
}