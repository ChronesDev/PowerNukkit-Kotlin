package cn.nukkit.item.enchantment.crossbow

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
class EnchantmentCrossbowQuickCharge @Since("1.4.0.0-PN") constructor() : EnchantmentCrossbow(Enchantment.ID_CROSSBOW_QUICK_CHARGE, "crossbowQuickCharge", Rarity.UNCOMMON) {
    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 12 + 20 * (level - 1)
    }

    @Override
    fun getMaxLevel(): Int {
        return 3
    }
}