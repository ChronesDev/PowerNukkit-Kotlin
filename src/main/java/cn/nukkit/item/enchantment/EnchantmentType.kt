package cn.nukkit.item.enchantment

import cn.nukkit.api.Since

/**
 * @author MagicDroidX (Nukkit Project)
 */
enum class EnchantmentType {
    ALL, ARMOR, ARMOR_HEAD, ARMOR_TORSO, ARMOR_LEGS, ARMOR_FEET, SWORD, DIGGER, FISHING_ROD, BREAKABLE, BOW, WEARABLE, TRIDENT, CROSSBOW;

    fun canEnchantItem(@Nonnull item: Item): Boolean {
        return if (this == ALL) {
            true
        } else if (this == BREAKABLE && item.getMaxDurability() >= 0) {
            true
        } else if (item is ItemArmor) {
            if (this == ARMOR || this == WEARABLE) {
                return true
            }
            when (this) {
                ARMOR_HEAD -> item.isHelmet()
                ARMOR_TORSO -> item.isChestplate()
                ARMOR_LEGS -> item.isLeggings()
                ARMOR_FEET -> item.isBoots()
                else -> false
            }
        } else {
            when (this) {
                SWORD -> item.isSword() && item !is ItemTrident
                DIGGER -> item.isPickaxe() || item.isShovel() || item.isAxe() || item.isHoe()
                BOW -> item is ItemBow
                FISHING_ROD -> item is ItemFishingRod
                WEARABLE -> item is ItemSkull || item.getBlock() is BlockCarvedPumpkin
                TRIDENT -> item is ItemTrident
                CROSSBOW -> item is ItemCrossbow
                else -> false
            }
        }
    }
}