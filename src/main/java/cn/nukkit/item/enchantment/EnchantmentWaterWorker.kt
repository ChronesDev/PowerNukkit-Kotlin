package cn.nukkit.item.enchantment

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EnchantmentWaterWorker : Enchantment(ID_WATER_WORKER, "waterWorker", Rarity.RARE, EnchantmentType.ARMOR_HEAD) {
    @Override
    override fun getMinEnchantAbility(level: Int): Int {
        return 1
    }

    @Override
    override fun getMaxEnchantAbility(level: Int): Int {
        return getMinEnchantAbility(level) + 40
    }
}