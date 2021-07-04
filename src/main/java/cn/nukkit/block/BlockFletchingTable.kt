package cn.nukkit.block

import cn.nukkit.item.ItemTool

class BlockFletchingTable : BlockSolid() {
    @get:Override
    override val id: Int
        get() = FLETCHING_TABLE

    @get:Override
    override val name: String
        get() = "Fletching Table"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val hardness: Double
        get() = 2.5

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @get:Override
    override val burnChance: Int
        get() = 5

    @Override
    override fun canHarvestWithHand(): Boolean {
        return true
    }
}