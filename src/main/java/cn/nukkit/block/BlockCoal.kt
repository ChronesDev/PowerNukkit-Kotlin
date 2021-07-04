package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author xtypr
 * @since 2015/11/24
 */
class BlockCoal : BlockSolid() {
    @get:Override
    override val id: Int
        get() = COAL_BLOCK

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
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 5

    @get:Override
    override val name: String
        get() = "Block of Coal"

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BLACK_BLOCK_COLOR
}