package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSeedsPumpkin @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(PUMPKIN_SEEDS, 0, count, "Pumpkin Seeds") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.PUMPKIN_STEM)
    }
}