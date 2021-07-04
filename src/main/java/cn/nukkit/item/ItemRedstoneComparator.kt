package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author CreeperFace
 */
class ItemRedstoneComparator(meta: Integer?, count: Int) : Item(COMPARATOR, meta, count, "Redstone Comparator") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(0, 1) {
    }

    init {
        this.block = Block.get(BlockID.UNPOWERED_COMPARATOR)
    }
}