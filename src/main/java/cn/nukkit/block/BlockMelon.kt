package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author Pub4Game
 * @since 2015/12/11
 */
class BlockMelon : BlockSolid() {
    @get:Override
    override val id: Int
        get() = MELON_BLOCK
    override val name: String
        get() = "Melon Block"
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 5

    @Override
    override fun getDrops(item: Item): Array<Item> {
        val random = Random()
        var count: Int = 3 + random.nextInt(5)
        val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1)
        }
        return arrayOf<Item>(
                ItemMelon(0, Math.min(9, count))
        )
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.LIME_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }
}