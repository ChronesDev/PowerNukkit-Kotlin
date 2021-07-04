package cn.nukkit.item

import cn.nukkit.block.BlockJungleSignPost

class ItemJungleSign @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemSign(JUNGLE_SIGN, meta, count, "Jungle Sign", BlockJungleSignPost()) {
    constructor(meta: Integer?) : this(meta, 1) {}
}