package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockOreDiamond : BlockSolid() {
    @get:Override
    override val id: Int
        get() = DIAMOND_ORE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_IRON

    @get:Override
    override val name: String
        get() = "Diamond Ore"

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= toolTier) {
            var count = 1
            val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
            if (fortune != null && fortune.getLevel() >= 1) {
                var i: Int = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1
                if (i < 0) {
                    i = 0
                }
                count = i + 1
            }
            arrayOf<Item>(
                    ItemDiamond(0, count)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val dropExp: Int
        get() = NukkitRandom().nextRange(3, 7)

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}