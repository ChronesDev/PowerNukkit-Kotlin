package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author Nukkit Project Team
 */
class BlockCarrot @JvmOverloads constructor(meta: Int = 0) : BlockCrops(meta) {
    @get:Override
    override val name: String
        get() = "Carrot Block"

    @get:Override
    override val id: Int
        get() = CARROT_BLOCK

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!isFullyGrown()) {
            return arrayOf<Item>(
                    Item.get(ItemID.CARROT)
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
        return arrayOf<Item>(
                Item.get(ItemID.CARROT, 0, drops)
        )
    }

    @Override
    override fun toItem(): Item {
        return Item.get(ItemID.CARROT)
    }
}