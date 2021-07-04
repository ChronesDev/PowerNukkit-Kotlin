package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
class BlockPotato @JvmOverloads constructor(meta: Int = 0) : BlockCrops(meta) {
    @get:Override
    override val name: String
        get() = "Potato Block"

    @get:Override
    override val id: Int
        get() = POTATO_BLOCK

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.POTATO)
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!isFullyGrown()) {
            return arrayOf<Item>(
                    Item.get(ItemID.POTATO)
            )
        }
        var drops = 2
        val attempts: Int = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING))
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        for (i in 0 until attempts) {
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                drops++
            }
        }
        return if (random.nextInt(5) < 1) { // 1/5, 0.2
            arrayOf<Item>(
                    Item.get(ItemID.POTATO, 0, drops),
                    Item.get(ItemID.POISONOUS_POTATO)
            )
        } else {
            arrayOf<Item>(
                    Item.get(ItemID.POTATO, 0, drops)
            )
        }
    }
}