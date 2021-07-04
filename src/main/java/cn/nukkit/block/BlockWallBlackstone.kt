package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockWallBlackstone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockWallBase(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Blackstone Wall"

    @get:Override
    override val id: Int
        get() = BLACKSTONE_WALL

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN
}