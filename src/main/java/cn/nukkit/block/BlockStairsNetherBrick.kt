package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsNetherBrick @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = NETHER_BRICKS_STAIRS

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val name: String
        get() = "Nether Bricks Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}