package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemIngotNetherite(meta: Integer?, count: Int) : Item(NETHERITE_INGOT, 0, count, "Netherite Ingot") {
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