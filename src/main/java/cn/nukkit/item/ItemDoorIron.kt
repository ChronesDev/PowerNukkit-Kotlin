package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemDoorIron @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(IRON_DOOR, 0, count, "Iron Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.IRON_DOOR_BLOCK)
    }
}