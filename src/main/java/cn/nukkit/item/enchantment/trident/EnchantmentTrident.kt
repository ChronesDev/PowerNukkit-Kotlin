package cn.nukkit.item.enchantment.trident

import cn.nukkit.api.DeprecationDetails

abstract class EnchantmentTrident @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?) : Enchantment(id, name, rarity, EnchantmentType.TRIDENT) {
    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit", replaceWith = "EnchantmentTrident(int id, String name, Rarity rarity)")
    protected constructor(id: Int, name: String?, weight: Int) : this(id, name, Rarity.fromWeight(weight)) {
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}