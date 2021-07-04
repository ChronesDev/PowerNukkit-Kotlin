package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemDoorWood @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(WOODEN_DOOR, 0, count, "Oak Door") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.WOODEN_DOOR_BLOCK)
    }
}