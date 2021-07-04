package cn.nukkit.item

import cn.nukkit.block.BlockKelp

class ItemKelp @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(KELP, meta, count, "Kelp") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = BlockKelp()
    }
}