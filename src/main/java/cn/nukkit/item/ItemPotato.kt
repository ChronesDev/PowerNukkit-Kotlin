package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemPotato : ItemEdible {
    constructor(meta: Integer?) : this(meta, 1) {}

    @JvmOverloads
    constructor(meta: Integer? = 0, count: Int = 1) : super(POTATO, meta, count, "Potato") {
        this.block = Block.get(BlockID.POTATO_BLOCK)
    }

    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {}
}