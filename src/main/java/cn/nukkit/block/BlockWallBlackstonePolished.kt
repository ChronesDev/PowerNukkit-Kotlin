package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockWallBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockWallBase(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_WALL

    @get:Override
    override val name: String
        get() = "Polished Blackstone Wall"

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 6.0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR
}