package cn.nukkit.item

import cn.nukkit.api.PowerNukkitDifference

@Since("1.4.0.0-PN")
class ItemLeggingsNetherite @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : ItemArmor(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings") {
    @Since("1.4.0.0-PN")
    constructor() : this(0, 1) {
    }

    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun isLeggings(): Boolean {
        return true
    }

    @Override
    override fun getTier(): Int {
        return ItemArmor.TIER_NETHERITE
    }

    @Override
    override fun getArmorPoints(): Int {
        return 6
    }

    @Override
    override fun getMaxDurability(): Int {
        return 555
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed toughness value")
    @Override
    override fun getToughness(): Int {
        return 3
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return true
    }
}