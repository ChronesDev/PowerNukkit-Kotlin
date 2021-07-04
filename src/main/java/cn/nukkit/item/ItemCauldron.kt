package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author CreeperFace (Nukkit Project)
 */
class ItemCauldron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(CAULDRON, meta, count, "Cauldron") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.CAULDRON_BLOCK)
    }
}