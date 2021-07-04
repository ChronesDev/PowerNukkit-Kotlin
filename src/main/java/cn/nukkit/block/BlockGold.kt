package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockGold : BlockSolid() {
    @get:Override
    override val id: Int
        get() = GOLD_BLOCK

    @get:Override
    override val name: String
        get() = "Gold Block"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_IRON

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GOLD_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}