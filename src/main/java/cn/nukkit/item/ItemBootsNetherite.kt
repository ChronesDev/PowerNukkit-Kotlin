package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemBootsNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemArmor(NETHERITE_BOOTS, meta, count, "Netherite Boots") {
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
    override fun isBoots(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 3
    }

    @Override
    override fun getMaxDurability(): Int {
        return 481
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