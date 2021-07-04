package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSeedsBeetroot @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BEETROOT_SEEDS, 0, count, "Beetroot Seeds") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.BEETROOT_BLOCK)
    }
}