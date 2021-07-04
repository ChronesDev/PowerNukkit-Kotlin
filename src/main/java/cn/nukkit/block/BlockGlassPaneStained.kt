package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
class BlockGlassPaneStained : BlockGlassPane {
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
        get() = STAINED_GLASS_PANE

    @get:Override
    override val name: String
        get() = dyeColor.getName().toString() + " stained glass pane"

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()
    val dyeColor: DyeColor
        get() = DyeColor.getByWoolData(getDamage())

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