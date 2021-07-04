package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSeedsWheat @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(WHEAT_SEEDS, 0, count, "Wheat Seeds") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.WHEAT_BLOCK)
    }
}