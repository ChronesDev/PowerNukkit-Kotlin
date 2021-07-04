package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockSlabWarped @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockSlab(meta, WARPED_DOUBLE_SLAB) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val slabName: String
        get() = "Warped"

    @get:Override
    override val id: Int
        get() = WARPED_SLAB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = SIMPLE_SLAB_PROPERTIES

    @Override
    fun isSameType(slab: BlockSlab): Boolean {
        return id == slab.getId()
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                toItem()
        )
    }

    @get:Override
    override val resistance: Double
        get() = 3

    @Override
    override fun toItem(): Item {
        return ItemBlock(this)
    }

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val burnAbility: Int
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CYAN_BLOCK_COLOR
}