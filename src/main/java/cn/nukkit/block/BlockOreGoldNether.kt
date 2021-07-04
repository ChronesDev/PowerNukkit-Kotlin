package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockOreGoldNether @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = NETHER_GOLD_ORE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Nether Gold Ore"

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY
        }
        val enchantment: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        var fortune = 0
        if (enchantment != null) {
            fortune = enchantment.getLevel()
        }
        val nukkitRandom = NukkitRandom()
        var count: Int = nukkitRandom.nextRange(2, 6)
        when (fortune) {
            0 -> {
            }
            1 -> if (nukkitRandom.nextRange(0, 2) === 0) {
                count *= 2
            }
            2 -> if (nukkitRandom.nextRange(0, 1) === 0) {
                count *= nukkitRandom.nextRange(2, 3)
            }
            3 -> if (nukkitRandom.nextRange(0, 4) < 3) {
                count *= nukkitRandom.nextRange(2, 4)
            }
            else -> if (nukkitRandom.nextRange(0, 4) < 3) {
                count *= nukkitRandom.nextRange(2, 4)
            }
        }
        return arrayOf<Item>(Item.get(ItemID.GOLD_NUGGET, 0, count))
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR
}