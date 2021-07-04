package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockOreIron : BlockSolid() {
    @get:Override
    override val id: Int
        get() = IRON_ORE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_STONE

    @get:Override
    override val name: String
        get() = "Iron Ore"

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}