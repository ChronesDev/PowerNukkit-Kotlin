package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
class ItemItemFrame @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(ITEM_FRAME, meta, count, "Item Frame") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.ITEM_FRAME_BLOCK)
    }
}