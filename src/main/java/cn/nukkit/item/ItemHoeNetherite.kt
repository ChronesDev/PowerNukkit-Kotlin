package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemHoeNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemTool(NETHERITE_HOE, meta, count, "Netherite Hoe") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun isHoe(): Boolean {
        return true
    }

    @Override
    override fun getAttackDamage(): Int {
        return 6
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_NETHERITE
    }

    @Override
    override fun getMaxDurability(): Int {
        return ItemTool.DURABILITY_NETHERITE
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}