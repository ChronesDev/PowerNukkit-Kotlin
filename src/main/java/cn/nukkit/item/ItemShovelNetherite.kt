package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemShovelNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemTool(NETHERITE_SHOVEL, meta, count, "Netherite Shovel") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_NETHERITE
    }

    @Override
    override fun isShovel(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_NETHERITE
    }

    @Override
    override fun getAttackDamage(): Int {
        return 5
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}