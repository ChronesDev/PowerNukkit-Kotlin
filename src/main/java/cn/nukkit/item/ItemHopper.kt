package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author CreeperFace
 * @since 13.5.2017
 */
class ItemHopper(meta: Integer?, count: Int) : Item(HOPPER, 0, count, "Hopper") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(meta, 1) {
    }

    init {
        this.block = Block.get(BlockID.HOPPER_BLOCK)
    }
}