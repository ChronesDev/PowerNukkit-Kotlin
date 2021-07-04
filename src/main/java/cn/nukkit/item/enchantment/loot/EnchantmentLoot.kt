package cn.nukkit.item.enchantment.loot

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EnchantmentLoot @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?, type: EnchantmentType?) : Enchantment(id, name, rarity, type) {
    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit", replaceWith = "EnchantmentLoot(int id, String name, Rarity rarity, EnchantmentType type)")
    protected constructor(id: Int, name: String?, weight: Int, type: EnchantmentType?) : this(id, name, Rarity.fromWeight(weight), type) {
    }

    @Override
    fun getMinEnchantAbility(level: Int): Int {
        return 15 + (level - 1) * 9
    }

    @Override
    fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 45 + level
    }

    @Override
    fun getMaxLevel(): Int {
        return 3
    }

    @Override
    fun checkCompatibility(enchantment: Enchantment): Boolean {
        return super.checkCompatibility(enchantment) && enchantment.id !== Enchantment.ID_SILK_TOUCH
    }
}