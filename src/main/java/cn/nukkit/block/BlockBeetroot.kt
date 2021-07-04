package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/11/22
 */
class BlockBeetroot @JvmOverloads constructor(meta: Int = 0) : BlockCrops(meta) {
    @get:Override
    override val id: Int
        get() = BEETROOT_BLOCK

    @get:Override
    override val name: String
        get() = "Beetroot Block"

    @Override
    override fun toItem(): Item {
        return ItemSeedsBeetroot()
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!isFullyGrown()) {
            return arrayOf<Item>(Item.get(ItemID.BEETROOT_SEEDS))
        }
        var seeds = 1
        val attempts: Int = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING))
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        for (i in 0 until attempts) {
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                seeds++
            }
        }
        return arrayOf<Item>(
                Item.get(ItemID.BEETROOT),
                Item.get(ItemID.BEETROOT_SEEDS, 0, seeds)
        )
    }
}