package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSlabBlackstonePolished : BlockSlab {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Int) : super(meta, POLISHED_BLACKSTONE_DOUBLE_SLAB) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(meta: Int, doubleSlab: Int) : super(meta, doubleSlab) {
    }

    @get:Override
    override val id: Int
        get() = POLISHED_BLACKSTONE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = SIMPLE_SLAB_PROPERTIES

    @get:Override
    override val slabName: String
        get() = "Polished Blackstone"

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return id == slab.getId()
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(toItem())
        } else Item.EMPTY_ARRAY
    }

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