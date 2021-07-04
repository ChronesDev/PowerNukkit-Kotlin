package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/11/22
 */
class BlockObsidianGlowing : BlockSolid() {
    @get:Override
    override val id: Int
        get() = GLOWING_OBSIDIAN

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Glowing Obsidian"

    @get:Override
    override val hardness: Double
        get() = 50

    @get:Override
    override val resistance: Double
        get() = 6000

    @get:Override
    override val lightLevel: Int
        get() = 12

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.OBSIDIAN))
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val toolTier: Int
        get() = ItemTool.TIER_DIAMOND

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isPickaxe() && item.getTier() > ItemTool.DIAMOND_PICKAXE) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}