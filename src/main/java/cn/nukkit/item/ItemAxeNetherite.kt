package cn.nukkit.item

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class ItemAxeNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemTool(NETHERITE_AXE, meta, count, "Netherite Axe") {
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
    override fun isAxe(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemTool.TIER_NETHERITE
    }

    @Override
    override fun getAttackDamage(): Int {
        return 8
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}