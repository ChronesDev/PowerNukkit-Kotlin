package cn.nukkit.item.enchantment.bow

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class EnchantmentBow @Since("1.4.0.0-PN") protected constructor(id: Int, name: String?, rarity: Rarity?) : Enchantment(id, name, rarity, EnchantmentType.BOW) {
    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit", replaceWith = "EnchantmentBow(int id, String name, Rarity rarity)")
    protected constructor(id: Int, name: String?, weight: Int) : this(id, name, Rarity.fromWeight(weight)) {
    }
}