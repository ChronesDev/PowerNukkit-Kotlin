package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockOreLapis : BlockSolid() {
    @get:Override
    override val id: Int
        get() = LAPIS_ORE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_STONE

    @get:Override
    override val name: String
        get() = "Lapis Lazuli Ore"

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() >= toolTier) {
            val random: ThreadLocalRandom = ThreadLocalRandom.current()
            var count: Int = 4 + random.nextInt(5)
            val fortune: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
            if (fortune != null && fortune.getLevel() >= 1) {
                var i: Int = random.nextInt(fortune.getLevel() + 2) - 1
                if (i < 0) {
                    i = 0
                }
                count *= i + 1
            }
            arrayOf<Item>(
                    MinecraftItemID.LAPIS_LAZULI.get(count)
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val dropExp: Int
        get() = NukkitRandom().nextRange(2, 5)

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}