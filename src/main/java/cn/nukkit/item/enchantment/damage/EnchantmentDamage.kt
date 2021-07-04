package cn.nukkit.item.enchantment.damage

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EnchantmentDamage @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?, protected val damageType: TYPE) : Enchantment(id, name, rarity, EnchantmentType.SWORD) {
    enum class TYPE {
        ALL, SMITE, ARTHROPODS
    }

    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit", replaceWith = "EnchantmentDamage(int id, String name, Rarity rarity, TYPE type)")
    protected constructor(id: Int, name: String?, weight: Int, type: TYPE?) : this(id, name, Rarity.fromWeight(weight), type) {
    }

    @Override
    fun checkCompatibility(enchantment: Enchantment?): Boolean {
        return enchantment !is EnchantmentDamage
    }

    @Override
    fun canEnchant(item: Item): Boolean {
        return item.isAxe() || super.canEnchant(item)
    }

    @Override
    fun getMaxLevel(): Int {
        return 5
    }

    @Override
    fun getName(): String {
        return "%enchantment.damage." + this.name
    }

    @Override
    fun isMajor(): Boolean {
        return true
    }

    @Override
    fun isItemAcceptable(item: Item): Boolean {
        return if (item.isAxe()) {
            true
        } else super.isItemAcceptable(item)
    }
}