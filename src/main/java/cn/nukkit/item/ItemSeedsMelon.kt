package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSeedsMelon @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(MELON_SEEDS, 0, count, "Melon Seeds") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.MELON_STEM)
    }
}