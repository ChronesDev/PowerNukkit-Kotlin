package cn.nukkit.item.randomitem

import cn.nukkit.item.Item

/**
 * @author Snake1999
 * @since 2016/1/15
 */
object Fishing {
    val ROOT_FISHING: Selector = putSelector(Selector(ROOT))
    val FISHES: Selector = putSelector(Selector(ROOT_FISHING), 0.85f)
    val TREASURES: Selector = putSelector(Selector(ROOT_FISHING), 0.05f)
    val JUNKS: Selector = putSelector(Selector(ROOT_FISHING), 0.1f)
    val FISH: Selector = putSelector(ConstantItemSelector(Item.RAW_FISH, FISHES), 0.6f)
    val SALMON: Selector = putSelector(ConstantItemSelector(Item.RAW_SALMON, FISHES), 0.25f)
    val CLOWNFISH: Selector = putSelector(ConstantItemSelector(Item.CLOWNFISH, FISHES), 0.02f)
    val PUFFERFISH: Selector = putSelector(ConstantItemSelector(Item.PUFFERFISH, FISHES), 0.13f)
    val TREASURE_BOW: Selector = putSelector(ConstantItemSelector(Item.BOW, TREASURES), 0.1667f)
    val TREASURE_ENCHANTED_BOOK: Selector = putSelector(ConstantItemSelector(Item.ENCHANTED_BOOK, TREASURES), 0.1667f)
    val TREASURE_FISHING_ROD: Selector = putSelector(ConstantItemSelector(Item.FISHING_ROD, TREASURES), 0.1667f)
    val TREASURE_NAME_TAG: Selector = putSelector(ConstantItemSelector(Item.NAME_TAG, TREASURES), 0.1667f)
    val TREASURE_SADDLE: Selector = putSelector(ConstantItemSelector(Item.SADDLE, TREASURES), 0.1667f)
    val JUNK_BOWL: Selector = putSelector(ConstantItemSelector(Item.BOWL, JUNKS), 0.12f)
    val JUNK_FISHING_ROD: Selector = putSelector(ConstantItemSelector(Item.FISHING_ROD, JUNKS), 0.024f)
    val JUNK_LEATHER: Selector = putSelector(ConstantItemSelector(Item.LEATHER, JUNKS), 0.12f)
    val JUNK_LEATHER_BOOTS: Selector = putSelector(ConstantItemSelector(Item.LEATHER_BOOTS, JUNKS), 0.12f)
    val JUNK_ROTTEN_FLESH: Selector = putSelector(ConstantItemSelector(Item.ROTTEN_FLESH, JUNKS), 0.12f)
    val JUNK_STICK: Selector = putSelector(ConstantItemSelector(Item.STICK, JUNKS), 0.06f)
    val JUNK_STRING_ITEM: Selector = putSelector(ConstantItemSelector(Item.STRING, JUNKS), 0.06f)
    val JUNK_WATTER_BOTTLE: Selector = putSelector(ConstantItemSelector(Item.POTION, Potion.NO_EFFECTS, JUNKS), 0.12f)
    val JUNK_BONE: Selector = putSelector(ConstantItemSelector(Item.BONE, JUNKS), 0.12f)
    val JUNK_TRIPWIRE_HOOK: Selector = putSelector(ConstantItemSelector(Item.TRIPWIRE_HOOK, JUNKS), 0.12f)
    fun getFishingResult(rod: Item?): Item? {
        var fortuneLevel = 0
        var lureLevel = 0
        if (rod != null) {
            if (rod.getEnchantment(Enchantment.ID_FORTUNE_FISHING) != null) {
                fortuneLevel = rod.getEnchantment(Enchantment.ID_FORTUNE_FISHING).getLevel()
            } else if (rod.getEnchantment(Enchantment.ID_LURE) != null) {
                lureLevel = rod.getEnchantment(Enchantment.ID_LURE).getLevel()
            }
        }
        return getFishingResult(fortuneLevel, lureLevel)
    }

    fun getFishingResult(fortuneLevel: Int, lureLevel: Int): Item? {
        val treasureChance: Float = NukkitMath.clamp(0.05f + 0.01f * fortuneLevel - 0.01f * lureLevel, 0, 1)
        val junkChance: Float = NukkitMath.clamp(0.05f - 0.025f * fortuneLevel - 0.01f * lureLevel, 0, 1)
        val fishChance: Float = NukkitMath.clamp(1 - treasureChance - junkChance, 0, 1)
        putSelector(FISHES, fishChance)
        putSelector(TREASURES, treasureChance)
        putSelector(JUNKS, junkChance)
        val result: Object = selectFrom(ROOT_FISHING)
        return if (result is Item) result else null
    }
}