package cn.nukkit.item

import cn.nukkit.block.BlockBirchSignPost

class ItemBirchSign @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemSign(BIRCH_SIGN, meta, count, "Birch Sign", BlockBirchSignPost()) {
    constructor(meta: Integer?) : this(meta, 1) {}
}