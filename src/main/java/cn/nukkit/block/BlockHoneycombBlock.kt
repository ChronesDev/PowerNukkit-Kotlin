package cn.nukkit.block

import cn.nukkit.item.ItemTool

class BlockHoneycombBlock : BlockSolid() {
    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HANDS_ONLY

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }

    @get:Override
    override val id: Int
        get() = HONEYCOMB_BLOCK

    @get:Override
    override val name: String
        get() = "Honeycomb Block"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}