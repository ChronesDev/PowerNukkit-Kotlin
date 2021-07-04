package cn.nukkit.item

import cn.nukkit.block.Block

class ItemDoorBirch @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BIRCH_DOOR, 0, count, "Birch Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.BIRCH_DOOR_BLOCK)
    }
}