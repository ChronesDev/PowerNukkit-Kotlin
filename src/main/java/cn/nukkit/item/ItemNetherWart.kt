package cn.nukkit.item

import cn.nukkit.block.BlockID

/**
 * @author Leonidius20
 * @since 22.03.17
 */
class ItemNetherWart @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(NETHER_WART, meta, count, "Nether Wart") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun setDamage(meta: Integer?) {
        block.setDataStorageFromInt(if (meta != null) meta else 0)
        super.setDamage(meta)
    }

    init {
        this.block = BlockState.of(BlockID.NETHER_WART_BLOCK, meta).getBlock()
    }
}