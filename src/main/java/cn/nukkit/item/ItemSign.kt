package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSign : Item {
    constructor(meta: Integer?) : this(meta, 1) {}
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?, block: BlockSignPost) : super(id, meta, count, name) {
        block = block
    }

    @JvmOverloads
    constructor(meta: Integer? = 0, count: Int = 1) : super(SIGN, 0, count, "Sign") {
        this.block = Block.get(BlockID.SIGN_POST)
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 16
    }
}