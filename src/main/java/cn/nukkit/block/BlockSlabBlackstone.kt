package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSlabBlackstone @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockSlab(meta, BLACKSTONE_DOUBLE_SLAB) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BLACKSTONE_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = SIMPLE_SLAB_PROPERTIES

    @get:Override
    override val slabName: String
        get() = "Blackstone Slab"

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return slab.getId() === id
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val resistance: Double
        get() = 6

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}