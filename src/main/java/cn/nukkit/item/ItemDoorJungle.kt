package cn.nukkit.item

import cn.nukkit.block.Block

class ItemDoorJungle @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(JUNGLE_DOOR, 0, count, "Jungle Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.JUNGLE_DOOR_BLOCK)
    }
}