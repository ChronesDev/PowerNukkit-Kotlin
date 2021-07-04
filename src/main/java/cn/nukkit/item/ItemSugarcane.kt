package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSugarcane @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(SUGARCANE, 0, count, "Sugar Cane") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.SUGARCANE_BLOCK)
    }
}