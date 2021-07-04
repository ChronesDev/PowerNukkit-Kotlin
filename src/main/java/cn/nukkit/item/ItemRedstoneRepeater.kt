package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author CreeperFace
 */
class ItemRedstoneRepeater(meta: Integer?, count: Int) : Item(REPEATER, meta, count, "Redstone Repeater") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(0, 1) {
    }

    init {
        this.block = Block.get(BlockID.UNPOWERED_REPEATER)
    }
}