package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemRedstone @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(REDSTONE, meta, count, "Redstone") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.REDSTONE_WIRE)
    }
}