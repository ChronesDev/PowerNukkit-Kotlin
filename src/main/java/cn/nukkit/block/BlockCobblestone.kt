package cn.nukkit.block

import cn.nukkit.item.ItemTool

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockCobblestone : BlockSolid() {
    @get:Override
    override val id: Int
        get() = COBBLESTONE

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
    override val name: String
        get() = "Cobblestone"

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}