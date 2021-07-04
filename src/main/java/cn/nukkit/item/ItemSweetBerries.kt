package cn.nukkit.item

import cn.nukkit.block.BlockSweetBerryBush

class ItemSweetBerries @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(SWEET_BERRIES, meta, count, "Sweet Berries") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = BlockSweetBerryBush()
    }
}