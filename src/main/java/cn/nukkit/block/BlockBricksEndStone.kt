package cn.nukkit.block

import cn.nukkit.item.Item

class BlockBricksEndStone : BlockSolid() {
    @get:Override
    override val name: String
        get() = "End Stone Bricks"

    @get:Override
    override val id: Int
        get() = END_BRICKS

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR
}