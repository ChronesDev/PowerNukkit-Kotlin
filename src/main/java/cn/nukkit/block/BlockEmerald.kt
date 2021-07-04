package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author xtypr
 * @since 2015/12/1
 */
class BlockEmerald : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Emerald Block"

    @get:Override
    override val id: Int
        get() = EMERALD_BLOCK

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val resistance: Double
        get() = 30

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_IRON

    @get:Override
    override val color: BlockColor
        get() = BlockColor.EMERALD_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}