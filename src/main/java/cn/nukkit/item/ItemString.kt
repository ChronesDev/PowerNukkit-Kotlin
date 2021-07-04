package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemString @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(STRING, meta, count, "String") {
    constructor(meta: Integer?) : this(meta, 1) {}

    init {
        this.block = Block.get(BlockID.TRIPWIRE)
    }
}