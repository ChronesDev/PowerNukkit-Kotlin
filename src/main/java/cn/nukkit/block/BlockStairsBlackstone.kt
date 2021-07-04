package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStairsBlackstone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockStairs(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Blackstone Stairs"

    @get:Override
    override val id: Int
        get() = BLACKSTONE_STAIRS

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 1.5

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}