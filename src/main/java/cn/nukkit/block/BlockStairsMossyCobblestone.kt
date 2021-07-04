package cn.nukkit.block

import cn.nukkit.item.ItemTool

class BlockStairsMossyCobblestone @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = MOSSY_COBBLESTONE_STAIRS

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
    override val name: String
        get() = "Mossy Cobblestone Stairs"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}