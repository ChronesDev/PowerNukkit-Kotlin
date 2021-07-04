package cn.nukkit.item.enchantment.protection

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EnchantmentProtection @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?, protected val protectionType: TYPE) : Enchantment(id, name, rarity, EnchantmentType.ARMOR) {
    enum class TYPE {
        ALL, FIRE, FALL, EXPLOSION, PROJECTILE
    }

    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit", replaceWith = "EnchantmentProtection(int id, String name, Rarity rarity, EnchantmentProtection.TYPE type)")
    protected constructor(id: Int, name: String?, weight: Int, type: TYPE?) : this(id, name, Rarity.fromWeight(weight), type) {
    }

    @Override
    fun canEnchant(@Nonnull item: Item?): Boolean {
        return item !is ItemElytra && super.canEnchant(item)
    }

    @Override
    fun checkCompatibility(enchantment: Enchantment): Boolean {
        return if (enchantment is EnchantmentProtection) {
            if ((enchantment as EnchantmentProtection).protectionType == protectionType) {
                false
            } else (enchantment as EnchantmentProtection).protectionType == TYPE.FALL || protectionType == TYPE.FALL
        } else super.checkCompatibility(enchantment)
    }

    @Override
    fun getMaxLevel(): Int {
        return 4
    }

    @Override
    fun getName(): String {
        return "%enchantment.protect." + this.name
    }

    fun getTypeModifier(): Double {
        return 0
    }

    @Override
    fun isMajor(): Boolean {
        return true
    }

    init {
        if (protectionType == TYPE.FALL) {
            protectionType = EnchantmentType.ARMOR_FEET
        }
    }
}