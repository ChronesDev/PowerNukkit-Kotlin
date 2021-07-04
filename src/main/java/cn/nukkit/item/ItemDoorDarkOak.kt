package cn.nukkit.item

import cn.nukkit.block.Block

class ItemDoorDarkOak @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(DARK_OAK_DOOR, 0, count, "Dark Oak Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.DARK_OAK_DOOR_BLOCK)
    }
}