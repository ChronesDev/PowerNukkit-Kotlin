package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoubleSlabBlackstonePolished @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockDoubleSlabBase(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_DOUBLE_SLAB

    @get:Override
    override val singleSlabId: Int
        get() = POLISHED_BLACKSTONE_SLAB

    @get:Override
    override val slabName: String
        get() = "Polished Blackstone"

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 6.0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR
}