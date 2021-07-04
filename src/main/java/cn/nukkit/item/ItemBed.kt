package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemBed @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(BED, meta, count, DyeColor.getByWoolData(meta).getName().toString() + " Bed") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    init {
        this.block = Block.get(BlockID.BED_BLOCK)
    }
}