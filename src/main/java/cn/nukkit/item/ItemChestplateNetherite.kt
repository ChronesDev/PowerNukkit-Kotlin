package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemChestplateNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemArmor(NETHERITE_CHESTPLATE, meta, count, "Netherite Chestplate") {
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
    override fun isChestplate(): Boolean {
        return true
    }

    @Override
    override fun getArmorPoints(): Int {
        return 8
    }

    @Override
    override fun getMaxDurability(): Int {
        return 592
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