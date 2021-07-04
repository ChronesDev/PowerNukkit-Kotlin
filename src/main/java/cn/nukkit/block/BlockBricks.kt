package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author Nukkit Project Team
 */
class BlockBricks : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Bricks"

    @get:Override
    override val id: Int
        get() = BRICKS_BLOCK

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.RED_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}