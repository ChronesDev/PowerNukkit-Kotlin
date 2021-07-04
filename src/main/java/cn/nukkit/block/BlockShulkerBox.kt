package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author PetteriM1
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockShulkerBox : BlockUndyedShulkerBox {
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
        get() = SHULKER_BOX

    @get:Override
    override val name: String
        get() = dyeColor.getName().toString() + " Shulker Box"

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()
    val dyeColor: DyeColor
        get() = DyeColor.getByWoolData(this.getDamage())

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.COLOR_BLOCK_PROPERTIES
    }
}