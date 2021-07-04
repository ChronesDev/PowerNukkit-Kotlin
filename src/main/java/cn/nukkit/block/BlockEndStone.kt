package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/12/1
 */
class BlockEndStone : BlockSolid() {
    @get:Override
    override val name: String
        get() = "End Stone"

    @get:Override
    override val id: Int
        get() = END_STONE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 45

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}