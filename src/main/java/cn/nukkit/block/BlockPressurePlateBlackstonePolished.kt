package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockPressurePlateBlackstonePolished : BlockPressurePlateStone {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        // Does nothing
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_PRESSURE_PLATE

    @get:Override
    override val name: String
        get() = "Polished Blackstone Pressure Plate"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR
}