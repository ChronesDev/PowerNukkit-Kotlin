package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockSmoothStone @PowerNukkitOnly constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = SMOOTH_STONE

    @get:Override
    override val name: String
        get() = "Smooth Stone"

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 10

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}