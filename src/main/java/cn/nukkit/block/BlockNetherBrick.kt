package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/12/7
 */
class BlockNetherBrick : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Nether Bricks"

    @get:Override
    override val id: Int
        get() = NETHER_BRICKS

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}