package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemCake @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(CAKE, 0, count, "Cake") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.CAKE_BLOCK)
    }
}