package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockOreRedstone : BlockSolid() {
    @get:Override
    override val id: Int
        get() = REDSTONE_ORE

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
        get() = "Redstone Ore"

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= toolTier) {
            var count: Int = Random().nextInt(2) + 4
            val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
            if (fortune != null && fortune.getLevel() >= 1) {
                count += Random().nextInt(fortune.getLevel() + 1)
            }
            arrayOf<Item>(
                    ItemRedstone(0, count)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, Block.get(BlockID.GLOWING_REDSTONE_ORE), false, true)
            return Level.BLOCK_UPDATE_WEAK
        }
        return 0
    }

    @get:Override
    override val dropExp: Int
        get() = NukkitRandom().nextRange(1, 5)

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}