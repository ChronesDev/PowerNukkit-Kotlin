package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author Snake1999
 * @since 2016/2/4
 */
class ItemFlowerPot @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(FLOWER_POT, meta, count, "Flower Pot") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(Block.FLOWER_POT_BLOCK)
    }
}