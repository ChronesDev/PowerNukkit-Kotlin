package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemCarrot @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(CARROT, 0, count, "Carrot") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.CARROT_BLOCK)
    }
}