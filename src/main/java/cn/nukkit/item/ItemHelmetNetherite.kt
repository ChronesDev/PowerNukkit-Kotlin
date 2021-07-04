package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemHelmetNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemArmor(NETHERITE_HELMET, meta, count, "Netherite Helmet") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_NETHERITE
    }

    @Override
    override fun isHelmet(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 407
    }

    @Override
    override fun getToughness(): Int {
        return 3
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}