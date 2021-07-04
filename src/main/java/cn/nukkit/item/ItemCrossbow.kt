package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemCrossbow @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemTool(CROSSBOW, meta, count, "Crossbow") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_CROSSBOW
    }

    @Override
    override fun getEnchantAbility(): Int {
        return 1
    }
}