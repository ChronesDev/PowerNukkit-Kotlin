package cn.nukkit.item

import cn.nukkit.block.Block

class ItemDoorAcacia @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(ACACIA_DOOR, 0, count, "Acacia Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.ACACIA_DOOR_BLOCK)
    }
}