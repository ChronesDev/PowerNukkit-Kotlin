package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockOreGold : BlockSolid() {
    @get:Override
    override val id: Int
        get() = GOLD_ORE

    @get:Override
    override val hardness: Double
        get() = 3

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_IRON

    @get:Override
    override val name: String
        get() = "Gold Ore"

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}