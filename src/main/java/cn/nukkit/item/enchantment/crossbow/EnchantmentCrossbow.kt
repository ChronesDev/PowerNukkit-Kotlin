package cn.nukkit.item.enchantment.crossbow

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
abstract class EnchantmentCrossbow @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?) : Enchantment(id, name, rarity, EnchantmentType.CROSSBOW) {
    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}