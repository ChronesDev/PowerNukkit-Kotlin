package cn.nukkit.item

import cn.nukkit.block.Block

class ItemDoorSpruce @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(SPRUCE_DOOR, 0, count, "Spruce Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.SPRUCE_DOOR_BLOCK)
    }
}