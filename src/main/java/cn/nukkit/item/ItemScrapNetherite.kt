package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemScrapNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(NETHERITE_SCRAP, 0, count, "Netherite Scrap") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}