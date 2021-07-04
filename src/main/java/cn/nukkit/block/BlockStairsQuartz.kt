package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsQuartz @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = QUARTZ_STAIRS

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val name: String
        get() = "Quartz Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.QUARTZ_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}