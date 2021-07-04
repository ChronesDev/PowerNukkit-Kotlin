package cn.nukkit.block

import cn.nukkit.item.Item

class BlockBricksRedNether : BlockNetherBrick() {
    @get:Override
    override val name: String
        get() = "Red Nether Bricks"

    @get:Override
    override val id: Int
        get() = RED_NETHER_BRICK

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR
}