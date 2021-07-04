package cn.nukkit.block

import cn.nukkit.item.ItemTool

class BlockStairsPrismarineBrick @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = PRISMARINE_BRICKS_STAIRS

    @get:Override
    override val hardness: Double
        get() = 1.5

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
    override val name: String
        get() = "Prismarine Brick Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIAMOND_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}