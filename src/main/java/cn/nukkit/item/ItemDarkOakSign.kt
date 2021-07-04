package cn.nukkit.item

import cn.nukkit.block.BlockDarkOakSignPost

class ItemDarkOakSign @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemSign(DARKOAK_SIGN, meta, count, "Dark Oak Sign", BlockDarkOakSignPost()) {
    constructor(meta: Integer?) : this(meta, 1) {}
}