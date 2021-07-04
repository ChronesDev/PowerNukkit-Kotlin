package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockWheat @JvmOverloads constructor(meta: Int = 0) : BlockCrops(meta) {
    @get:Override
    override val name: String
        get() = "Wheat Block"

    @get:Override
    override val id: Int
        get() = WHEAT_BLOCK

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.SEEDS)
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        // https://minecraft.gamepedia.com/Fortune#Seeds
        if (!isFullyGrown()) {
            return arrayOf<Item>(ItemSeedsWheat())
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        var count = 0
        val attempts: Int = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING))
        // Fortune increases the number of tests for the distribution, and thus the maximum number of drops, by 1 per level
        for (i in 0 until attempts) {
            // The binomial distribution in the default case is created by rolling three times (n=3) with a drop probability of 57%
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                count++
            }
        }
        return if (count > 0) {
            arrayOf<Item>(Item.get(ItemID.WHEAT), Item.get(ItemID.WHEAT_SEEDS, 0, count))
        } else {
            arrayOf<Item>(Item.get(ItemID.WHEAT))
        }
    }
}