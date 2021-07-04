package cn.nukkit.item.enchantment

import cn.nukkit.block.BlockID

class EnchantmentVanishingCurse : Enchantment(ID_VANISHING_CURSE, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.BREAKABLE) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 25
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }

    @Override
    override fun canEnchant(item: Item): Boolean {
        return when (item.getId()) {
            ItemID.SKULL, ItemID.COMPASS -> true
            else -> {
                if (item.getId() < 255 && item.getBlock() != null && item.getBlock().getId() === BlockID.CARVED_PUMPKIN) {
                    true
                } else super.canEnchant(item)
            }
        }
    }
}