package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockBlackstoneGilded @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = GILDED_BLACKSTONE

    @get:Override
    override val name: String
        get() = "Gilded Blackstone"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @Override
    override fun getDrops(item: Item): Array<Item> {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY
        }
        val dropOdds: Int
        var fortune = 0
        val enchantment: Enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING)
        if (enchantment != null) {
            fortune = enchantment.getLevel()
        }
        dropOdds = when (fortune) {
            0 -> 10
            1 -> 7
            2 -> 4
            else -> 1
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        return if (dropOdds > 1 && random.nextInt(dropOdds) !== 0) {
            arrayOf<Item>(toItem())
        } else arrayOf<Item>(Item.get(ItemID.GOLD_NUGGET, 0, random.nextInt(2, 6)))
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 6
}