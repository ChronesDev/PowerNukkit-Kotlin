package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class EnchantmentBindingCurse : Enchantment(ID_BINDING_CURSE, "curse.binding", Rarity.VERY_RARE, EnchantmentType.WEARABLE) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 25
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return 50
    }
}