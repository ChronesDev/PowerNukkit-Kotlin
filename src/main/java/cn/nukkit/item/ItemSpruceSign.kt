package cn.nukkit.item

import cn.nukkit.block.BlockSpruceSignPost

class ItemSpruceSign @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemSign(SPRUCE_SIGN, meta, count, "Spruce Sign", BlockSpruceSignPost()) {
    constructor(meta: Integer?) : this(meta, 1) {}
}