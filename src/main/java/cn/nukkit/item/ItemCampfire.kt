package cn.nukkit.item

import cn.nukkit.block.BlockCampfire

class ItemCampfire @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(CAMPFIRE, meta, count, "Campfire") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    init {
        this.block = BlockCampfire()
    }
}