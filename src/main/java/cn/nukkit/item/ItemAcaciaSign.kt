package cn.nukkit.item

import cn.nukkit.block.BlockAcaciaSignPost

class ItemAcaciaSign @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemSign(ACACIA_SIGN, meta, count, "Acacia Sign", BlockAcaciaSignPost()) {
    constructor(meta: Integer?) : this(meta, 1) {}
}