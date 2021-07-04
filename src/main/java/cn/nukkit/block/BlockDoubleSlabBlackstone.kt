package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockDoubleSlabBlackstone @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor(meta: Int) : BlockDoubleSlabBase(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val slabName: String
        get() = "Double Blackstone Slab"

    @get:Override
    override val id: Int
        get() = BLACKSTONE_DOUBLE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlab.SIMPLE_SLAB_PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val singleSlabId: Int
        get() = BLACKSTONE_SLAB

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}